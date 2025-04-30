package com.pickgo.domain.payment.dto;

import com.pickgo.domain.payment.entity.Payment;
import com.pickgo.domain.payment.entity.PaymentStatus;

public record PaymentDetailResponse(
		Long id,
		Long amount,
		PaymentStatus paymentStatus,
		Long reservationId
) {
	public static PaymentDetailResponse from(Payment payment) {
		return new PaymentDetailResponse(
				payment.getId(),
				payment.getAmount(),
				payment.getStatus(),
				payment.getReservation().getId()
		);
	}
}
