package com.pickgo.domain.reservation.service;

import com.pickgo.domain.area.seat.entity.Seat;
import com.pickgo.domain.area.seat.repository.SeatRepository;
import com.pickgo.domain.member.entity.Member;
import com.pickgo.domain.member.repository.MemberRepository;
import com.pickgo.domain.payment.entity.Payment;
import com.pickgo.domain.payment.entity.PaymentStatus;
import com.pickgo.domain.payment.repository.PaymentRepository;
import com.pickgo.domain.payment.service.PaymentService;
import com.pickgo.domain.performance.entity.PerformanceSession;
import com.pickgo.domain.performance.repository.PerformanceSessionRepository;
import com.pickgo.domain.reservation.dto.request.ReservationCreateRequest;
import com.pickgo.domain.reservation.dto.response.ReservationDetailResponse;
import com.pickgo.domain.reservation.dto.response.ReservationSimpleResponse;
import com.pickgo.domain.reservation.entity.PendingSeat;
import com.pickgo.domain.reservation.entity.Reservation;
import com.pickgo.domain.reservation.enums.ReservationStatus;
import com.pickgo.domain.reservation.repository.ReservationRepository;
import com.pickgo.global.dto.PageResponse;
import com.pickgo.global.exception.BusinessException;
import com.pickgo.global.response.RsCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.pickgo.domain.area.seat.entity.SeatStatus.AVAILABLE;
import static com.pickgo.domain.area.seat.entity.SeatStatus.PENDING;
import static com.pickgo.global.response.RsCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final PerformanceSessionRepository sessionRepository;
    private final SeatRepository seatRepository;
    private final MemberRepository memberRepository;
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;

    public ReservationSimpleResponse createReservation(
            UUID memberId,
            ReservationCreateRequest request
    ) {

        // 0. 유저 검증
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new BusinessException(MEMBER_NOT_FOUND)
        );

        // 1. 공연 회차 검증
        PerformanceSession performanceSession = sessionRepository.findById(request.performance_session_id()).orElseThrow(
                () -> new BusinessException(PERFORMANCE_SESSION_NOT_FOUND)
        );

        // 2. 좌석 조회 및 검증
        List<Seat> seats = seatRepository.findAllById(request.seatIds());
        if (seats.size() != request.seatIds().size()) {
            throw new BusinessException(NOT_FOUND);
        }

        // 여기부터 예약 트랜지션이므로 하나로 묶어야함
        try {
            // 3. 좌석 상태 확인 후 상태 변경(예약이 가능하다는 것이므로 예약 절차 진행)
            for (Seat seat : seats) {
                if (seat.getStatus() != AVAILABLE) {
                    throw new BusinessException(SEAT_CONFLICT);
                }
                seat.setStatus(PENDING);
            }

            // 4. 예약 내역 생성
            // 4-1. 총 가격을 알기위해서는 pendingSeat의 가격 정보를 가져와함 -> area별로 다른 가격 계산 필요
            int totalPrice = seats.stream()
                    .mapToInt(seat -> seat.getPerformanceArea().getPrice()).sum();

            // 4-2. 예약 생성
            Reservation reservation = Reservation.builder()
                    .member(member)
                    .performanceSession(performanceSession)
                    .status(ReservationStatus.RESERVED)
                    .totalPrice(totalPrice)
                    .build();

            // 5. 멤버 연관관계
            member.addReservation(reservation);

            // 6. PendingSeat 생성 및 연관관계
            List<PendingSeat> pendingSeats = seats.stream()
                    .map(seat -> PendingSeat.builder()
                            .seat(seat)
                            .reservation(reservation)
                            .build()
                    ).toList();
            reservation.updatePendingSeats(pendingSeats);

            // 7. 예약 저장
            reservationRepository.save(reservation);

            return ReservationSimpleResponse.from(reservation);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new BusinessException(SEAT_CONFLICT);
        }
    }

    @Transactional(readOnly = true)
    public ReservationDetailResponse getReservation(Long id, UUID memberId) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(RESERVATION_NOT_FOUND));

        // 본인 예약인지 확인
        if (!reservation.getMember().getId().equals(memberId)) {
            throw new BusinessException(RsCode.FORBIDDEN);
        }

        return ReservationDetailResponse.from(reservation);
    }

    @Transactional(readOnly = true)
    public PageResponse<ReservationSimpleResponse> getMyReservationList(UUID memberId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

        // 오직 PAID 또는 CANCELED 상태만 조회
        Page<Reservation> reservations = reservationRepository
                .findByMemberIdAndStatusIn(memberId, List.of(ReservationStatus.PAID, ReservationStatus.CANCELED), pageable);

        return PageResponse.from(reservations, ReservationSimpleResponse::from);
    }

    /***
     * 예약 삭제는 예약하기(예약 생성) 후 뒤로가기 할 때 발생한다.
     * 뒤로가기는 의미 없는 예약 생성이기때문에 삭제가 필요
     */
    public void deleteReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(RsCode.NOT_FOUND));

        // 1. 상태 체크 (RESERVED 상태인 자리만 취소가능)
        if (reservation.getStatus() != ReservationStatus.RESERVED) {
            throw new BusinessException(RsCode.INVALID_RESERVATION_STATE);
        }

        // 2. 자리를 다시 원복
        for (PendingSeat pendingSeat : reservation.getPendingSeats()) {
            pendingSeat.getSeat().setStatus(AVAILABLE);
        }

        // 3. 예약 삭제
        reservationRepository.delete(reservation);
    }

    /***
     * 예약 내역 조회 후, 예약을 취소할때 사용된다
     * 내부적으로 결제 취소도 동반한다.
     */
    public void cancelReservation(Long id) {
        // 1. 예약과 결제를 조회
        Reservation reservation = reservationRepository.findById(id).orElseThrow(
                () -> new BusinessException(RESERVATION_NOT_FOUND)
        );
        Payment payment = paymentRepository.findByReservation(reservation).orElseThrow(
                () -> new BusinessException(NOT_FOUND)
        );

        // 2. TOSS 결제 취소 + 상태 변경
        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            paymentService.cancelPayment(payment.getId());
        }

        // 3. 예약 상태 변경
        reservation.cancel();

        // 4. 예약된 좌석 복구
        reservation.releaseSeats();
    }
}