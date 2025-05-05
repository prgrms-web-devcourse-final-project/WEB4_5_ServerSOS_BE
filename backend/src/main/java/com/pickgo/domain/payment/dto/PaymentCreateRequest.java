package com.pickgo.domain.payment.dto;

import com.pickgo.domain.payment.entity.Payment;
import com.pickgo.domain.payment.entity.PaymentStatus;
import com.pickgo.domain.reservation.entity.Reservation;
import jakarta.validation.constraints.NotNull;

public record PaymentCreateRequest(
        @NotNull Integer amount,
        @NotNull Long reservationId
) {
    public Payment toEntity(Reservation reservation, String orderId) {
        return Payment.builder()
                .amount(reservation.getTotalPrice())
                .status(PaymentStatus.PENDING)
                .orderId(orderId)
                .reservation(reservation)
                .build();
    }
}