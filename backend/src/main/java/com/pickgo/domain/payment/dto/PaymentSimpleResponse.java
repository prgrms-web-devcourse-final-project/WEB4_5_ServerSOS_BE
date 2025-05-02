package com.pickgo.domain.payment.dto;

import com.pickgo.domain.payment.entity.Payment;
import com.pickgo.domain.payment.entity.PaymentStatus;

public record PaymentSimpleResponse(
        Long id,
        Integer amount,
        PaymentStatus paymentStatus
) {
    public static PaymentSimpleResponse from(Payment payment) {
        return new PaymentSimpleResponse(
                payment.getId(),
                payment.getAmount(),
                payment.getStatus()
        );
    }
}
