package com.pickgo.domain.area.area.dto;

import com.pickgo.domain.area.area.entity.PerformanceArea;

public record PerformanceAreaResponse(
        Long id,
        String name,
        String grade,
        int price
) {
    public static PerformanceAreaResponse from(PerformanceArea area) {
        return new PerformanceAreaResponse(area.getId(), area.getName().getValue(), area.getGrade().getValue(), area.getPrice());
    }
}
