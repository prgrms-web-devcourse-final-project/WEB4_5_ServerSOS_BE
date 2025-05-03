package com.pickgo.domain.performance.dto;

import com.pickgo.domain.area.area.dto.PerformanceAreaResponse;
import com.pickgo.domain.performance.entity.Performance;
import com.pickgo.domain.performance.entity.PerformanceIntro;
import com.pickgo.domain.performance.entity.PerformanceState;
import com.pickgo.domain.performance.entity.PerformanceType;
import com.pickgo.domain.venue.dto.VenueResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record PerformanceDetailResponse(
        Long id,
        String name,
        String poster,
        PerformanceState state,
        PerformanceType type,
        String casts,
        String runtime,
        String minAge,
        LocalDate startDate,
        LocalDate endDate,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt,
        VenueResponse venue,
        List<String> images,
        List<PerformanceAreaResponse> areas,
        List<PerformanceSessionResponse> sessions
) {
    public static PerformanceDetailResponse from(Performance performance) {
        return new PerformanceDetailResponse(
                performance.getId(),
                performance.getName(),
                performance.getPoster(),
                performance.getState(),
                performance.getType(),
                performance.getCasts(),
                performance.getRuntime(),
                performance.getMinAge(),
                performance.getStartDate(),
                performance.getEndDate(),
                performance.getCreatedAt(),
                performance.getModifiedAt(),
                new VenueResponse(performance.getVenue().getName(), performance.getVenue().getAddress()),
                performance.getPerformanceIntros().stream()
                        .map(PerformanceIntro::getIntro_image)
                        .toList(),
                performance.getPerformanceAreas().stream()
                        .map(PerformanceAreaResponse::from)
                        .toList(),
                performance.getPerformanceSessions().stream()
                        .map(PerformanceSessionResponse::from)
                        .toList());
    }
}
