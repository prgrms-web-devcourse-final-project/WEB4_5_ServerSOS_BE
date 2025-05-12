package com.pickgo.domain.area.seat.dto;

import com.pickgo.domain.area.seat.entity.ReservedSeat;
import com.pickgo.domain.area.seat.entity.SeatStatus;

public record SeatUpdateResponse(
        Long sessionId,
        Long areaId,
        String row,
        Integer number,
        SeatStatus status
) {
    public static SeatUpdateResponse from(ReservedSeat seat) {
        return new SeatUpdateResponse(
                seat.getPerformanceSession().getId(),
                seat.getPerformanceArea().getId(),
                seat.getRow(),
                seat.getNumber(),
                seat.getStatus()
        );
    }
}