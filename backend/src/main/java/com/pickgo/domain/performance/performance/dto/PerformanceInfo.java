package com.pickgo.domain.performance.performance.dto;

import com.pickgo.domain.performance.performance.entity.Performance;

public record PerformanceInfo(
        String name,
        String poster,
        String runtime,
        String type,
        String state
) {
    public static PerformanceInfo from(Performance p) {
        return new PerformanceInfo(
                p.getName(),
                p.getPoster(),
                p.getRuntime(),
                p.getType().getValue(),
                p.getState().getValue()
        );
    }
}
