package com.pickgo.domain.log.dto;

import com.pickgo.domain.log.entity.ReservationHistory;
import com.pickgo.domain.reservation.enums.ReservationStatus;

import java.time.LocalDateTime;

public record ReservationLogResponse(
        Long reservationId,
        Long performanceSessionId,
        ReservationStatus status,
        int totalPrice,
        LocalDateTime reservationTime,
        BaseLogResponse base
) {
    public static ReservationLogResponse from(ReservationHistory h) {
        return new ReservationLogResponse(
                h.getReservationId(),
                h.getPerformanceSessionId(),
                h.getStatus(),
                h.getTotalPrice(),
                h.getReservationTime(),
                BaseLogResponse.from(h)
        );
    }
}
