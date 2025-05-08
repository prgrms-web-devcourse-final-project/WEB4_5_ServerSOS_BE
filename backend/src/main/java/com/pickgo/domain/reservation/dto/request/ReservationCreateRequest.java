package com.pickgo.domain.reservation.dto.request;


import java.util.List;

public record ReservationCreateRequest(
        Long performance_session_id,
        List<SeatDto> seats
) {
    public record SeatDto(
            long areaId,
            int row,
            int column
    ) {
    }
}
