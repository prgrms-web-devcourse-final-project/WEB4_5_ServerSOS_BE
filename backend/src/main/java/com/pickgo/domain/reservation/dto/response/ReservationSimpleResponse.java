package com.pickgo.domain.reservation.dto.response;

import com.pickgo.domain.area.seat.dto.SeatSimpleResponse;
import com.pickgo.domain.reservation.entity.Reservation;
import com.pickgo.domain.reservation.enums.ReservationStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record ReservationSimpleResponse(
        Long id,
        UUID memberId,
        Long performance_session_id,
        int total_price,
        ReservationStatus status,
        LocalDateTime reservation_time,
        List<SeatSimpleResponse> seats
) {
    public static ReservationSimpleResponse from(Reservation reservation) {
        return ReservationSimpleResponse.builder()
                .id(reservation.getId())
                .memberId(reservation.getMember().getId())
                .performance_session_id(reservation.getPerformanceSession().getId())
                .total_price(reservation.getTotalPrice())
                .status(reservation.getStatus())
                .reservation_time(reservation.getCreatedAt())
                .seats(
                        reservation.getPendingSeats().stream()
                                .map(pendingSeat -> SeatSimpleResponse.from(pendingSeat.getSeat()))
                                .toList()
                )
                .build();
    }
}
