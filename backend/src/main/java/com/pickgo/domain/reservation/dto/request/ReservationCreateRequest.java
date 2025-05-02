package com.pickgo.domain.reservation.dto.request;


import java.util.List;

public record ReservationCreateRequest(
        Long performance_session_id,
        List<Long> seatIds
) {
}
