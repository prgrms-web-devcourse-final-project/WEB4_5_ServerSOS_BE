package com.pickgo.domain.reservation.dto.response;

import com.pickgo.domain.area.seat.dto.SeatSimpleResponse;
import com.pickgo.domain.performance.entity.Performance;
import com.pickgo.domain.performance.entity.PerformanceSession;
import com.pickgo.domain.reservation.entity.Reservation;
import com.pickgo.domain.reservation.enums.ReservationStatus;
import com.pickgo.domain.venue.entity.Venue;
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
        SessionInfo session,
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
                .session(SessionInfo.from(session))
                .venue(VenueInfo.from(performance.getVenue()))
                .seats(reservation.getPendingSeats().stream()
                        .map(p -> SeatSimpleResponse.from(p.getSeat()))
                        .toList())
                .build();
    }

    public record PerformanceInfo(
            String name,
            String poster,
            String runtime,
            String type,
            String state
    ) {
        public static PerformanceInfo from(Performance p) {
            return new PerformanceInfo(
                    p.getName(),
                    p.getPoster(),
                    p.getRuntime(),
                    p.getType().getValue(),
                    p.getState().getValue()
            );
        }
    }

    public record SessionInfo(
            Long id,
            LocalDateTime performanceTime
    ) {
        public static SessionInfo from(PerformanceSession session) {
            return new SessionInfo(
                    session.getId(),
                    session.getPerformanceTime()
            );
        }
    }

    public record VenueInfo(
            String name,
            String address
    ) {
        public static VenueInfo from(Venue venue) {
            return new VenueInfo(
                    venue.getName(),
                    venue.getAddress()
            );
        }
    }
}
