package com.pickgo.domain.payment.dto;

import com.pickgo.domain.payment.entity.Payment;
import com.pickgo.domain.payment.entity.PaymentStatus;
import com.pickgo.domain.reservation.entity.Reservation;
import jakarta.validation.constraints.NotNull;

public record PaymentCreateRequest(
		@NotNull Long amount,
		@NotNull Long reservationId
) {
	public Payment toEntity(Reservation reservation) {
		return Payment.builder()
				.amount(amount)
				.status(PaymentStatus.PENDING) // 기본값으로 설정한다고 가정
				.reservation(reservation)
				.build();
	}
}