package com.pickgo.domain.payment.service;

import com.pickgo.domain.member.entity.Member;
import com.pickgo.domain.member.repository.MemberRepository;
import com.pickgo.domain.payment.config.TossPaymentConfig;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final TossPaymentConfig tossPaymentConfig;

    @Transactional
    public PaymentDetailResponse createPayment(PaymentCreateRequest request) {
        Reservation reservation = reservationRepository.findById(request.reservationId())
                .orElseThrow(() -> new BusinessException(RsCode.NOT_FOUND));

        String orderId = UUID.randomUUID().toString(); // 서버에서 UUID 생성

        Payment payment = request.toEntity(reservation, orderId);
        paymentRepository.save(payment);

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

        String paymentKey = payment.getPaymentKey();
        if (paymentKey == null || paymentKey.isBlank()) {
            throw new BusinessException(RsCode.BAD_REQUEST);
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", tossPaymentConfig.getAuthorizations());
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> payload = new HashMap<>();
            payload.put("cancelReason", "결제취소"); // 프론트에서 취소 사유 작성해야하나 임시로 고정

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
            RestTemplate restTemplate = new RestTemplate();

            restTemplate.postForEntity(
                    TossPaymentConfig.apiUrl + "/" + paymentKey + "/cancel",
                    entity,
                    String.class
            );

            payment.setStatus(PaymentStatus.CANCELLED); // DB 상태 변경
            return PaymentDetailResponse.from(payment);

        } catch (HttpClientErrorException e) {
            throw new BusinessException(RsCode.PAYMENT_TOSS_CANCEL_FAILED);
        }
    }

    @Transactional
    public PaymentDetailResponse confirmPayment(PaymentConfirmRequest req) {
//        Payment payment = getEntity(id);
        Payment payment = paymentRepository.findByOrderId(req.orderId())
                .orElseThrow(() -> new BusinessException(RsCode.NOT_FOUND));


        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new BusinessException(RsCode.BAD_REQUEST);
        }

        if (!payment.getAmount().equals(req.amount()) || !payment.getOrderId().equals(req.orderId())) {
            payment.setStatus(PaymentStatus.FAILED);
            throw new BusinessException(RsCode.PAYMENT_INTEGRITY_ERROR);
        }

        // TossPayments 결제 승인 요청
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", tossPaymentConfig.getAuthorizations());
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> payload = new HashMap<>();
            payload.put("paymentKey", req.paymentKey());
            payload.put("orderId", req.orderId());
            payload.put("amount", req.amount());

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.tosspayments.com/v1/payments/confirm",
                    entity,
                    String.class
            );

            // 성공 시 상태 변경
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setPaymentKey(req.paymentKey());

        } catch (HttpClientErrorException e) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new BusinessException(RsCode.PAYMENT_TOSS_FAILED);
        }

        return PaymentDetailResponse.from(payment);
    }

    private Payment getEntity(Long id) {
        return paymentRepository.findById(id).orElseThrow(() -> new BusinessException(RsCode.NOT_FOUND));
    }
}
