package com.pickgo.domain.area.area.dto;

import com.pickgo.domain.area.area.entity.PerformanceArea;
import com.pickgo.domain.area.seat.dto.SeatSimpleResponse;
import com.pickgo.domain.area.seat.entity.ReservedSeat;

import java.util.List;

public record PerformanceAreaDetailResponse(
        Long id,
        String name,
        String grade,
        int price,
        int rowCount,
        int colCount,
        List<SeatSimpleResponse> reservedSeats
) {
    public static PerformanceAreaDetailResponse from(PerformanceArea area, List<ReservedSeat> reservedSeats) {
        return new PerformanceAreaDetailResponse(
                area.getId(),
                area.getName().getValue(),
                area.getGrade().getValue(),
                area.getPrice(),
                area.getRowCount(),
                area.getColCount(),
                reservedSeats.stream().map(SeatSimpleResponse::from).toList()
        );
    }
}
