package com.pickgo.domain.area.seat.dto;

import com.pickgo.domain.area.seat.entity.Seat;
import com.pickgo.domain.area.seat.entity.SeatStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SeatSimpleResponse(
        Long id,
        String row,
        Integer number,
        SeatStatus status,
        LocalDateTime created_at
) {
    public static SeatSimpleResponse from(Seat seat) {
        return SeatSimpleResponse.builder()
                .id(seat.getId())
                .row(seat.getRow())
                .number(seat.getNumber())
                .status(seat.getStatus())
                .created_at(seat.getCreatedAt())
                .build();
    }
}
