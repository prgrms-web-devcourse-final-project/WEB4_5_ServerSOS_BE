package com.pickgo.domain.performance.performance.dto;

import com.pickgo.domain.performance.performance.entity.PerformanceSession;

import java.time.LocalDateTime;

public record PerformanceSessionResponse(
        Long id,
        LocalDateTime time,
        LocalDateTime reserveOpenAt
) {
    public static PerformanceSessionResponse from(PerformanceSession session) {
        return new PerformanceSessionResponse(session.getId(), session.getPerformanceTime(), session.getReserveOpenAt());
    }
}
