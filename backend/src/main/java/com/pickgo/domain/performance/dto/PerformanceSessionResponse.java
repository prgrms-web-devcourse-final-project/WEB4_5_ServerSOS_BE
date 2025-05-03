package com.pickgo.domain.performance.dto;

import com.pickgo.domain.performance.entity.PerformanceSession;

import java.time.LocalDateTime;

public record PerformanceSessionResponse(
        Long id,
        LocalDateTime performanceTime
) {
    public static PerformanceSessionResponse from(PerformanceSession session) {
        return new PerformanceSessionResponse(session.getId(), session.getPerformanceTime());
    }
}
