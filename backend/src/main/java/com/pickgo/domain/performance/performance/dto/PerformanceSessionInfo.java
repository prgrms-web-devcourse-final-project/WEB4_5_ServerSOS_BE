package com.pickgo.domain.performance.performance.dto;

import com.pickgo.domain.performance.performance.entity.PerformanceSession;

import java.time.LocalDateTime;

public record PerformanceSessionInfo(
        Long id,
        LocalDateTime performanceTime
) {
    public static PerformanceSessionInfo from(PerformanceSession session) {
        return new PerformanceSessionInfo(
                session.getId(),
                session.getPerformanceTime()
        );
    }
}
