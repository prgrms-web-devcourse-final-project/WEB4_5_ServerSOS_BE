package com.pickgo.domain.area.seat.dto;

import com.pickgo.domain.area.seat.entity.Seat;

import java.time.LocalDateTime;

public record SeatResponse(
        Long seatId,
        Long performanceAreaId,
        Long performanceSeesionId,
        String row,
        Integer number,
        String status,
        LocalDateTime createdAt
) {
    public static SeatResponse from(Seat seat) {
        return new SeatResponse(
                seat.getId(),
                seat.getPerformanceArea().getId(),
                seat.getPerformanceSession().getId(),
                seat.getRow(),
                seat.getNumber(),
                seat.getStatus().name(),
                seat.getCreatedAt()
        );
    }

}
