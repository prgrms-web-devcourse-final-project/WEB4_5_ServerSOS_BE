package com.pickgo.domain.area.seat.dto;

import com.pickgo.domain.area.seat.entity.ReservedSeat;
import com.pickgo.domain.area.seat.entity.SeatStatus;

public record SeatUpdateRequest(
        Long sessionId,
        Long areaId,
        String row,
        Integer number,
        SeatStatus status
) {
    public static SeatUpdateRequest from(ReservedSeat seat) {
        return new SeatUpdateRequest(
                seat.getPerformanceSession().getId(),
                seat.getPerformanceArea().getId(),
                seat.getRow(),
                seat.getNumber(),
                seat.getStatus()
        );
    }
}