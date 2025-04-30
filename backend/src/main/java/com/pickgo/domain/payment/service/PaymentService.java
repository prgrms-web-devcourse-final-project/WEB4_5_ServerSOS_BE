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
        Reservation reservation = reservationRepository.findById(request.reservationId())
                .orElseThrow(() -> new BusinessException(RsCode.NOT_FOUND));

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

    @Transactional
    public PaymentDetailResponse cancelPayment(Long id) {
        Payment payment = getEntity(id);
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new BusinessException(RsCode.BAD_REQUEST);
        }
//        paymentRepository.delete(payment);
        payment.setStatus(PaymentStatus.CANCELLED);
        return PaymentDetailResponse.from(payment);
    }

    @Transactional
    public PaymentDetailResponse confirmPayment(Long id, PaymentConfirmRequest req) {
        Payment payment = getEntity(id);
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new BusinessException(RsCode.BAD_REQUEST);
        }
        payment.setStatus(PaymentStatus.COMPLETED);
        return PaymentDetailResponse.from(payment);
    }

    private Payment getEntity(Long id) {
        return paymentRepository.findById(id).orElseThrow(() -> new BusinessException(RsCode.NOT_FOUND));
    }
}
