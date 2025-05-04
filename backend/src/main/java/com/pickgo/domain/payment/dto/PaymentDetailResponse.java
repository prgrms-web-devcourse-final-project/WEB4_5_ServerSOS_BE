package com.pickgo.domain.payment.dto;

import com.pickgo.domain.payment.entity.Payment;
import com.pickgo.domain.payment.entity.PaymentStatus;

public record PaymentDetailResponse(
        Long id,
        Integer amount,
        PaymentStatus paymentStatus,
        Long reservationId,
        String orderId // Toss 위젯에 필요, 프론트에서 사용
) {
    public static PaymentDetailResponse from(Payment payment) {
        return new PaymentDetailResponse(
                payment.getId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getReservation().getId(),
                payment.getOrderId()
        );
    }
}

