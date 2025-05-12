package com.pickgo.domain.payment.service;

import com.pickgo.domain.area.seat.entity.SeatStatus;
import com.pickgo.domain.area.seat.event.SeatStatusChangedEvent;
import com.pickgo.domain.area.seat.repository.ReservedSeatRepository;
import com.pickgo.domain.member.entity.Member;
import com.pickgo.domain.member.repository.MemberRepository;
import com.pickgo.domain.payment.dto.PaymentConfirmRequest;
import com.pickgo.domain.payment.dto.PaymentCreateRequest;
import com.pickgo.domain.payment.dto.PaymentDetailResponse;
import com.pickgo.domain.payment.dto.PaymentSimpleResponse;
import com.pickgo.domain.payment.entity.Payment;
import com.pickgo.domain.payment.entity.PaymentStatus;
import com.pickgo.domain.payment.repository.PaymentRepository;
import com.pickgo.domain.reservation.entity.Reservation;
import com.pickgo.domain.reservation.enums.ReservationStatus;
import com.pickgo.domain.reservation.repository.ReservationRepository;
import com.pickgo.global.dto.PageResponse;
import com.pickgo.global.exception.BusinessException;
import com.pickgo.global.response.RsCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final TossService tossService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ReservedSeatRepository reservedSeatRepository;

    @Transactional
    public PaymentDetailResponse createPayment(PaymentCreateRequest request) {
        // 예약 정보를 조회합니다. 만약 없다면 잘못된 결제 생성 이므로 예외를 발생시킵니다.
        Reservation reservation = reservationRepository.findById(request.reservationId())
                .orElseThrow(() -> new BusinessException(RsCode.NOT_FOUND));

        if (reservation.getStatus() != ReservationStatus.RESERVED) {
            throw new BusinessException(RsCode.INVALID_RESERVATION_STATE);
        }

        String orderId = UUID.randomUUID().toString(); // 서버에서 UUID 생성

        Payment payment = request.toEntity(reservation, orderId);
        paymentRepository.save(payment);

        return PaymentDetailResponse.from(payment);
    }


    @Transactional(readOnly = true)
    public PageResponse<PaymentSimpleResponse> getMyPayments(UUID memberId, Pageable pageable) {
        // 내 예약 정보 목록을 조회합니다. 클라이언트로 부터 받은 AuthenticationPrincipal 통해 멤버를 조회합니다
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(RsCode.NOT_FOUND));

        // 멤버 정보를 이용하여 결제 정보를 조회합니다. 페이징 처리를 위해 pageable 객체를 사용합니다.
        Page<Payment> payments = paymentRepository.findByReservationMember(member, pageable);
        return PageResponse.from(payments, PaymentSimpleResponse::from);
    }

    @Transactional(readOnly = true)
    public PaymentDetailResponse getPaymentDetail(Long id) {
        // 결제 정보 단건을 조회합니다. 결제 ID를 통해 조회합니다.
        Payment payment = getEntity(id);
        return PaymentDetailResponse.from(payment);
    }

    /***
     * 내부적으로 예약 취소에서 사용
     */
    @Transactional
    public void cancelPayment(Long id) {
        Payment payment = getEntity(id);

        // 결제 상태가 COMPLETED가 아니라면 결제가 된것이 아니라 취소 불가.
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new BusinessException(RsCode.BAD_REQUEST);
        }

        // paymentKey가 없다면 결제 취소 요청을 보낼 수 없음. 결제가 된것도 아님.
        String paymentKey = payment.getPaymentKey();
        if (paymentKey == null || paymentKey.isBlank()) {
            throw new BusinessException(RsCode.BAD_REQUEST);
        }

        // TossPayments 결제 취소 요청
        tossService.cancelPayment(paymentKey);

        // 상태 변경
        payment.cancel();
    }

    // BusinessException이 발생하면 트랜잭션이 롤백되므로 noRollbackFor 설정. FAILED 상태 저장용
    @Transactional(noRollbackFor = BusinessException.class)
    public PaymentDetailResponse confirmPayment(PaymentConfirmRequest req) {
        // 결제 과정에서 생성된 payment를 찾아야하므로 클라이언트에 있는 정보인 orderId로 조회
        Payment payment = paymentRepository.findByOrderId(req.orderId())
                .orElseThrow(() -> new BusinessException(RsCode.NOT_FOUND));

        // 결제가 만료되었다면 결제 만료 예외
        if (payment.getStatus() == PaymentStatus.EXPIRED) {
            throw new BusinessException(RsCode.PAYMENT_EXPIRED);
        }

        // 결제 상태가 PENDING이 아니라면 결제가 된것이 아님. 결제 승인 불가.
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new BusinessException(RsCode.BAD_REQUEST);
        }

        // DB에 저장된 결제 금액 & 주문 ID가 클라이언트의 정보와 일치하지 않는다면 무결성 깨짐. 결제 승인 불가.
        if (!payment.getAmount().equals(req.amount()) || !payment.getOrderId().equals(req.orderId())) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new BusinessException(RsCode.PAYMENT_INTEGRITY_ERROR);
        }

        try {
            tossService.confirmPayment(req.paymentKey(), req.orderId(), req.amount());
        }
        catch (HttpClientErrorException e) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new BusinessException(RsCode.PAYMENT_TOSS_FAILED);
        }

        // confirm 성공 시,

        // 1. 결제 상태 변경
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaymentKey(req.paymentKey());

        // 2. 예약 상태 변경
        Reservation reservation = payment.getReservation();
        reservation.setStatus(ReservationStatus.PAID);

        // 3. 좌석 상태 변경
        reservation.getReservedSeats().forEach(seat -> {
            seat.setStatus(SeatStatus.RESERVED);        // 1. 좌석 상태 변경
            reservedSeatRepository.save(seat);          // 2. 좌석 상태 DB 반영
            applicationEventPublisher.publishEvent(new SeatStatusChangedEvent(seat)); // 좌석 상태 변경 이벤트를 spring에 발행, 구독하고 있는 SSE리스너가 변경 감지 -> 알림 발송
        });

        return PaymentDetailResponse.from(payment);
    }

    private Payment getEntity(Long id) {
        // findById용 메서드
        return paymentRepository.findById(id).orElseThrow(() -> new BusinessException(RsCode.NOT_FOUND));
    }
}
