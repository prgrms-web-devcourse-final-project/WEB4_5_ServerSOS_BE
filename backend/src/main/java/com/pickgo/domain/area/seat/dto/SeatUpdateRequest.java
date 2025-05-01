package com.pickgo.domain.area.seat.dto;

import com.pickgo.domain.area.seat.entity.SeatStatus;

public record SeatUpdateRequest(
        Long seatId,
        SeatStatus status
) {

}
