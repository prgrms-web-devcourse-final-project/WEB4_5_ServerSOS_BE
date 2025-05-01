package com.pickgo.domain.reservation.dto.response;

import com.pickgo.domain.reservation.entity.Reservation;
import com.pickgo.domain.reservation.enums.ReservationStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ReservationSimpleResponse(
        Long id,
        UUID memberId,
        Long performance_session_id,
        int total_price,
        ReservationStatus status,
        LocalDateTime reservation_time
) {
    public ReservationSimpleResponse from(Reservation reservation) {
        return ReservationSimpleResponse.builder()
                .id(reservation.getId())
                .memberId(reservation.getMember().getId())
                .performance_session_id(reservation.getPerformanceSession().getId())
                .total_price(reservation.getTotalPrice())
                .status(reservation.getStatus())
                .reservation_time(reservation.getCreatedAt())
                .build();
    }
}
