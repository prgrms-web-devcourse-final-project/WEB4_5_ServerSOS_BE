package com.pickgo.domain.area.seat.dto;

import com.pickgo.domain.area.seat.entity.Seat;
import com.pickgo.domain.area.seat.entity.SeatStatus;

import java.time.LocalDateTime;

public record SeatResponse(
        Long seatId,
        Long performanceAreaId,
        Long performanceSeesionId,
        String row,
        Integer number,
        SeatStatus status,
        LocalDateTime createdAt
) {
    public static SeatResponse from(Seat seat) {
        return new SeatResponse(
                seat.getId(),
                seat.getPerformanceArea().getId(),
                seat.getPerformanceSession().getId(),
                seat.getRow(),
                seat.getNumber(),
                seat.getStatus(),
                seat.getCreatedAt()
        );
    }

}
