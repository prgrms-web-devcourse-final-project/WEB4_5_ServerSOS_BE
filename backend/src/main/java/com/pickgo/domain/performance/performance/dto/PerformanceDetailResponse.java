package com.pickgo.domain.performance.performance.dto;

import com.pickgo.domain.performance.area.area.dto.PerformanceAreaSimpleResponse;
import com.pickgo.domain.performance.performance.entity.Performance;
import com.pickgo.domain.performance.performance.entity.PerformanceIntro;
import com.pickgo.domain.performance.venue.dto.VenueResponse;

import java.time.LocalDate;
import java.util.List;

public record PerformanceDetailResponse(
        Long id,
        String name,
        String poster,
        String state,
        String type,
        String casts,
        String runtime,
        String minAge,
        LocalDate startDate,
        LocalDate endDate,
        VenueResponse venue,
        List<String> images,
        List<PerformanceAreaSimpleResponse> areas,
        List<PerformanceSessionResponse> sessions
) {
    public static PerformanceDetailResponse from(Performance performance) {
        return new PerformanceDetailResponse(
                performance.getId(),
                performance.getName(),
                performance.getPoster(),
                performance.getState().getValue(),
                performance.getType().getValue(),
                performance.getCasts(),
                performance.getRuntime(),
                performance.getMinAge(),
                performance.getStartDate(),
                performance.getEndDate(),
                new VenueResponse(performance.getVenue().getName(), performance.getVenue().getAddress()),
                performance.getPerformanceIntros().stream()
                        .map(PerformanceIntro::getIntroImage)
                        .toList(),
                performance.getPerformanceAreas().stream()
                        .map(PerformanceAreaSimpleResponse::from)
                        .toList(),
                performance.getPerformanceSessions().stream()
                        .map(PerformanceSessionResponse::from)
                        .toList());
    }
}
