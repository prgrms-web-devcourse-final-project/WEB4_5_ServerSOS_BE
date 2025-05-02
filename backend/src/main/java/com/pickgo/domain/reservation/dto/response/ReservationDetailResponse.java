package com.pickgo.domain.reservation.dto.response;

import com.pickgo.domain.area.seat.dto.SeatSimpleResponse;
import com.pickgo.domain.performance.dto.PerformanceInfo;
import com.pickgo.domain.performance.dto.PerformanceSessionInfo;
import com.pickgo.domain.performance.dto.VenueInfo;
import com.pickgo.domain.performance.entity.Performance;
import com.pickgo.domain.performance.entity.PerformanceSession;
import com.pickgo.domain.reservation.entity.Reservation;
import com.pickgo.domain.reservation.enums.ReservationStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record ReservationDetailResponse(
        // 예약 정보
        Long id,
        UUID memberId,
        int total_price,
        ReservationStatus status,
        LocalDateTime reservation_time,
        PerformanceInfo performance,
        PerformanceSessionInfo session,
        VenueInfo venue,
        List<SeatSimpleResponse> seats
) {
    public static ReservationDetailResponse from(Reservation reservation) {
        PerformanceSession session = reservation.getPerformanceSession();
        Performance performance = session.getPerformance();

        return ReservationDetailResponse.builder()
                .id(reservation.getId())
                .memberId(reservation.getMember().getId())
                .status(reservation.getStatus())
                .total_price(reservation.getTotalPrice())
                .reservation_time(reservation.getCreatedAt())
                .performance(PerformanceInfo.from(performance))
                .session(PerformanceSessionInfo.from(session))
                .venue(VenueInfo.from(performance.getVenue()))
                .seats(reservation.getPendingSeats().stream()
                        .map(p -> SeatSimpleResponse.from(p.getSeat()))
                        .toList())
                .build();
    }
}
