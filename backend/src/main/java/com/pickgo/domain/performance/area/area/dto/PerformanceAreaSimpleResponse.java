package com.pickgo.domain.performance.area.area.dto;

import com.pickgo.domain.performance.area.area.entity.PerformanceArea;

public record PerformanceAreaSimpleResponse(
        Long id,
        String name,
        String grade,
        int price
) {
    public static PerformanceAreaSimpleResponse from(PerformanceArea area) {
        return new PerformanceAreaSimpleResponse(area.getId(), area.getName().getValue(), area.getGrade().getValue(), area.getPrice());
    }
}
