package com.pickgo.domain.payment.service;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public PaymentDetailResponse createPayment(PaymentCreateRequest request) {
        // 1. 예약 가져옴
        Reservation reservation = reservationRepository.findById(request.reservationId())
                .orElseThrow(() -> new BusinessException(RsCode.NOT_FOUND));

        // 2. 결제 생성
        Payment payment = paymentRepository.save(request.toEntity(reservation));
        return PaymentDetailResponse.from(payment);
    }

    @Transactional(readOnly = true)
    public PageResponse<PaymentSimpleResponse> getMyPayments(UUID memberId, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(RsCode.NOT_FOUND));

        Page<Payment> payments = paymentRepository.findByReservationMember(member, pageable);
        return PageResponse.from(payments, PaymentSimpleResponse::from);
    }

    @Transactional(readOnly = true)
    public PaymentDetailResponse getPaymentDetail(Long id) {
        Payment payment = getEntity(id);
        return PaymentDetailResponse.from(payment);
    }

    /***
     * PG사 내부적으로 취소시 결제 삭제
     * 예약 상태가 아직 RESERVED면 결제 다시 생성 가능
     * 결제 취소 자체는 예약 취소 시에만 가능
     */
    @Transactional
    public void deletePayment(Long id) {
        Payment payment = getEntity(id);

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new BusinessException(RsCode.INVALID_PAYMENT_STATE);
        }
        paymentRepository.delete(payment);
    }

    @Transactional
    public PaymentDetailResponse confirmPayment(Long id, PaymentConfirmRequest req) {
        Payment payment = getEntity(id);

        // 이미 만료된 결제는 거절 -> 10분이 지나서 결제 만료
        if (payment.getStatus() == PaymentStatus.EXPIRED) {
            throw new BusinessException(RsCode.PAYMENT_EXPIRED);
        }

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new BusinessException(RsCode.BAD_REQUEST);
        }

        Reservation reservation = payment.getReservation();

        // 결제 승인 시 결제 완료 처리 및 예약 PAID 처리
        payment.setStatus(PaymentStatus.COMPLETED);
        reservation.setStatus(ReservationStatus.PAID);

        return PaymentDetailResponse.from(payment);
    }

    private Payment getEntity(Long id) {
        return paymentRepository.findById(id).orElseThrow(() -> new BusinessException(RsCode.NOT_FOUND));
    }
}
