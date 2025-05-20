package com.pickgo.domain.reservation.service;

import com.pickgo.domain.log.enums.ActionType;
import com.pickgo.domain.member.member.entity.Member;
import com.pickgo.domain.member.member.repository.MemberRepository;
import com.pickgo.domain.payment.entity.Payment;
import com.pickgo.domain.payment.entity.PaymentStatus;
import com.pickgo.domain.payment.repository.PaymentRepository;
import com.pickgo.domain.payment.service.PaymentService;
import com.pickgo.domain.performance.area.area.entity.PerformanceArea;
import com.pickgo.domain.performance.area.area.repository.PerformanceAreaRepository;
import com.pickgo.domain.performance.area.seat.entity.ReservedSeat;
import com.pickgo.domain.performance.area.seat.entity.SeatStatus;
import com.pickgo.domain.performance.area.seat.event.SeatStatusChangedEvent;
import com.pickgo.domain.performance.area.seat.repository.ReservedSeatRepository;
import com.pickgo.domain.performance.performance.entity.PerformanceSession;
import com.pickgo.domain.performance.performance.repository.PerformanceSessionRepository;
import com.pickgo.domain.reservation.dto.request.ReservationCreateRequest;
import com.pickgo.domain.reservation.dto.response.ReservationDetailResponse;
import com.pickgo.domain.reservation.dto.response.ReservationSimpleResponse;
import com.pickgo.domain.reservation.entity.Reservation;
import com.pickgo.domain.reservation.enums.ReservationStatus;
import com.pickgo.domain.reservation.repository.ReservationRepository;
import com.pickgo.global.exception.BusinessException;
import com.pickgo.global.logging.util.LogWriter;
import com.pickgo.global.response.PageResponse;
import com.pickgo.global.response.RsCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.pickgo.global.response.RsCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final PerformanceSessionRepository sessionRepository;
    private final ReservedSeatRepository seatRepository;
    private final MemberRepository memberRepository;
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final LogWriter logWriter;
    private final PerformanceAreaRepository areaRepository;
    private final ApplicationEventPublisher applicationEventPublisher;


    public ReservationSimpleResponse createReservation(
            UUID memberId,
            ReservationCreateRequest request
    ) {
        // 1. Member, Performance 검증 및 데이터 가져오기
        Member member = validateMember(memberId);
        PerformanceSession session = validateSession(request.performance_session_id());
        validateReservationTime(session);

        // 2. 요청 좌석 생성
        List<ReservedSeat> seats = generateReservedSeats(session, request.seats());

        // 3. 1,2번을 통해 예약 생성
        return createReservationWithSeats(member, session, seats);
    }

    private List<ReservedSeat> generateReservedSeats(PerformanceSession session, List<ReservationCreateRequest.SeatRequest> seatRequests) {
        return seatRequests.stream()
                .map(dto -> createReservedSeat(session, dto))
                .toList();
    }

    private ReservedSeat createReservedSeat(PerformanceSession session, ReservationCreateRequest.SeatRequest dto) {
        PerformanceArea area = validateArea(dto.areaId());

        validateSeatPosition(area, dto);

        ReservedSeat seat = ReservedSeat.builder()
                .performanceArea(area)
                .row(String.valueOf((char) ('A' + dto.row() - 1)))
                .number(dto.column())
                .status(SeatStatus.PENDING)
                .build();

        seat.setPerformanceSession(session);
        return seat;
    }

    private ReservationSimpleResponse createReservationWithSeats(
            Member member, PerformanceSession session, List<ReservedSeat> seats
    ) {
        try {
            int totalPrice = seats.stream()
                    .mapToInt(seat -> seat.getPerformanceArea().getPrice()).sum();

            // 예약 생성
            Reservation reservation = Reservation.builder()
                    .member(member)
                    .performanceSession(session)
                    .status(ReservationStatus.RESERVED)
                    .totalPrice(totalPrice)
                    .build();

            // 연관관계 세팅
            member.addReservation(reservation);
            reservation.updateReservedSeats(seats);
            seats.forEach(seat -> {
                seat.setReservation(reservation);
                seat.setPerformanceSession(session);
            });

            // 좌석 및 예약 저장
            reservationRepository.save(reservation);
            seatRepository.saveAll(seats);


            // 예약 직후에 이벤트를 발행 (좌석이 점유되었음을 알림)
            seats.forEach(seat -> {
                applicationEventPublisher.publishEvent(new SeatStatusChangedEvent(seat));
            });

            logWriter.writeReservationLog(reservation, ActionType.RESERVATION_CREATED);

            return ReservationSimpleResponse.from(reservation);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(SEAT_CONFLICT);
        }
    }

    @Transactional(readOnly = true)
    public ReservationDetailResponse getReservation(Long reservationId, UUID memberId) {
        Reservation reservation = validateReservation(reservationId);

        // 본인 예약인지 확인
        validateReservationOwner(memberId, reservation);

        return ReservationDetailResponse.from(reservation);
    }

    private void validateReservationOwner(UUID memberId, Reservation reservation) {
        if (!reservation.getMember().getId().equals(memberId)) {
            throw new BusinessException(RsCode.FORBIDDEN);
        }
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

        // 2. 좌석 활성화 및 SSE 이벤트 발행
        releaseSeatsAndPublishEvent(reservation);

        // 3. 예약 삭제
        reservationRepository.delete(reservation);

        // 4. 로깅
        logWriter.writeReservationLog(reservation, ActionType.RESERVATION_DELETED);
    }

    /***
     * 예약 내역 조회 후, 예약을 취소할때 사용된다
     * 내부적으로 결제 취소도 동반한다.
     */
    public void cancelReservation(Long reservationId) {
        // 1. 예약과 결제를 조회
        Reservation reservation = validateReservation(reservationId);
        Payment payment = validatePayment(reservation);

        // 2. TOSS 결제 취소 + 상태 변경
        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            paymentService.cancelPayment(payment.getId());
        }

        // 3. 예약 상태 변경
        reservation.cancel();

        // 4. 좌석 해제 및 이벤트 발행
        releaseSeatsAndPublishEvent(reservation);

        // 5. 예약된 좌석 복구
        reservation.getReservedSeats().clear();

        // 6. 로깅
        logWriter.writeReservationLog(reservation, ActionType.RESERVATION_CANCELED);
    }

    private Payment validatePayment(Reservation reservation) {
        return paymentRepository.findByReservation(reservation).orElseThrow(
                () -> new BusinessException(NOT_FOUND)
        );
    }

    private void releaseSeatsAndPublishEvent(Reservation reservation) {
        reservation.getReservedSeats().forEach(seat -> {
            seat.setStatus(SeatStatus.RELEASED);
            applicationEventPublisher.publishEvent(new SeatStatusChangedEvent(seat));
        });
    }

    private void validateSeatPosition(PerformanceArea area, ReservationCreateRequest.SeatRequest dto) {
        if (dto.row() < 1 || dto.row() > area.getRowCount()
                || dto.column() < 1 || dto.column() > area.getColCount()) {
            throw new BusinessException(INVALID_SEAT_POSITION);
        }
    }

    private PerformanceArea validateArea(Long areaId) {
        return areaRepository.findById(areaId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND));
    }

    private void validateReservationTime(PerformanceSession performanceSession) {
        if (performanceSession.getReserveOpenAt().isAfter(LocalDateTime.now())
                || performanceSession.getPerformanceTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException(RESERVATION_UNAVAILABLE);
        }
    }

    private PerformanceSession validateSession(Long performanceSessionId) {
        return sessionRepository.findById(performanceSessionId).orElseThrow(
                () -> new BusinessException(PERFORMANCE_SESSION_NOT_FOUND)
        );
    }

    private Member validateMember(UUID memberId) {
        return memberRepository.findById(memberId).orElseThrow(
                () -> new BusinessException(MEMBER_NOT_FOUND)
        );
    }

    private Reservation validateReservation(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(RESERVATION_NOT_FOUND));
    }
}