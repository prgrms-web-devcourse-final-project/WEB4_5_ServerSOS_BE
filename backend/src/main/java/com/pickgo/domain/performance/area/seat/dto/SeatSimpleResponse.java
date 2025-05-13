package com.pickgo.domain.performance.area.seat.dto;

import com.pickgo.domain.performance.area.seat.entity.ReservedSeat;
import com.pickgo.domain.performance.area.seat.entity.SeatStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SeatSimpleResponse(
        Long id,
        String AreaName,
        String row,
        Integer number,
        SeatStatus status,
        LocalDateTime createdAt
) {
    public static SeatSimpleResponse from(ReservedSeat seat) {
        return SeatSimpleResponse.builder()
                .id(seat.getId())
                .AreaName(seat.getPerformanceArea().getName().getValue())
                .row(seat.getRow())
                .number(seat.getNumber())
                .status(seat.getStatus())
                .createdAt(seat.getCreatedAt())
                .build();
    }
}
