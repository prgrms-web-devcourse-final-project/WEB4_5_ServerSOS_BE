package com.pickgo.domain.log.dto;

import com.pickgo.domain.log.entity.PaymentHistory;
import com.pickgo.domain.payment.entity.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentLogResponse(
        Long paymentId,
        Long reservationId,
        String orderId,
        Integer amount,
        PaymentStatus status,
        LocalDateTime paymentTime,
        BaseLogResponse base
) {
    public static PaymentLogResponse from(PaymentHistory h) {
        return new PaymentLogResponse(
                h.getPaymentId(),
                h.getReservationId(),
                h.getOrderId(),
                h.getAmount(),
                h.getStatus(),
                h.getPaymentTime(),
                BaseLogResponse.from(h)
        );
    }
}
