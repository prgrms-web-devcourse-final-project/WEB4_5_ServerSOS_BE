package com.pickgo.domain.area.area.dto;

import com.pickgo.domain.area.area.entity.AreaGrade;
import com.pickgo.domain.area.area.entity.AreaName;
import com.pickgo.domain.area.area.entity.PerformanceArea;

public record PerformanceAreaResponse(
        Long id,
        AreaName name,
        AreaGrade grade,
        int price
) {
    public static PerformanceAreaResponse from(PerformanceArea area) {
        return new PerformanceAreaResponse(area.getId(), area.getName(), area.getGrade(), area.getPrice());
    }
}
