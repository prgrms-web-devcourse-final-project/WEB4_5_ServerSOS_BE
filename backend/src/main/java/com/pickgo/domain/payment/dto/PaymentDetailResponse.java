package com.pickgo.domain.payment.dto;

import com.pickgo.domain.payment.entity.Payment;
import com.pickgo.domain.payment.entity.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentDetailResponse(
        Long id,
        Integer amount,
        String orderId, // Toss 위젯에 필요, 프론트에서 사용
        Long reservationId,
        String performanceName, // 예약 상세 페이지에서 사용
        PaymentStatus paymentStatus,
        LocalDateTime createdAt
) {
    public static PaymentDetailResponse from(Payment payment) {
        return new PaymentDetailResponse(
                payment.getId(),
                payment.getAmount(),
                payment.getOrderId(),
                payment.getReservation().getId(),
                payment.getReservation().getPerformanceSession().getPerformance().getName(),
                payment.getStatus(),
                payment.getCreatedAt()
        );
    }
}

