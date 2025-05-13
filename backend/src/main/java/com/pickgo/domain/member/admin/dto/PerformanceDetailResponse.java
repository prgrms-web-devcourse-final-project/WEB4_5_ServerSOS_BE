package com.pickgo.domain.member.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pickgo.domain.performance.area.area.entity.AreaGrade;
import com.pickgo.domain.performance.area.area.entity.AreaName;
import com.pickgo.domain.performance.area.area.entity.PerformanceArea;
import com.pickgo.domain.performance.performance.entity.Performance;
import com.pickgo.domain.performance.performance.entity.PerformanceIntro;
import com.pickgo.domain.performance.performance.entity.PerformanceSession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PerformanceDetailResponse {
    private Long id;
    private String name;
    private String poster;
    private String state;
    private String type;
    private String casts;
    private String runtime;
    private String minAge;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    private VenueResponse venue;
    private List<String> introImages;
    private List<PerformanceAreaResponse> areas;
    private List<PerformanceSessionResponse> sessions;

    public static PerformanceDetailResponse from(Performance p) {
        return new PerformanceDetailResponse(
                p.getId(),
                p.getName(),
                p.getPoster(),
                p.getState().name(),
                p.getType().name(),
                p.getCasts(),
                p.getRuntime(),
                p.getMinAge(),
                p.getStartDate().atStartOfDay(),
                p.getEndDate().atStartOfDay(),
                p.getCreatedAt(),
                p.getModifiedAt(),
                new VenueResponse(p.getVenue().getName(), p.getVenue().getAddress()),
                p.getPerformanceIntros().stream()
                        .map(PerformanceIntro::getIntroImage)
                        .collect(Collectors.toList()),
                p.getPerformanceAreas().stream()
                        .map(PerformanceAreaResponse::from)
                        .collect(Collectors.toList()),
                p.getPerformanceSessions().stream()
                        .map(PerformanceSessionResponse::from)
                        .collect(Collectors.toList())
        );
    }

    public record VenueResponse(String name, String address) {
    }

    public record PerformanceAreaResponse(Long id, AreaName name, AreaGrade grade, int price) {
        public static PerformanceAreaResponse from(PerformanceArea a) {
            return new PerformanceAreaResponse(a.getId(), a.getName(), a.getGrade(), a.getPrice());
        }
    }

    public record PerformanceSessionResponse(
            @JsonProperty("performance_session_id") Long id,
            @JsonProperty("performance_time") LocalDateTime time
    ) {
        public static PerformanceSessionResponse from(PerformanceSession s) {
            return new PerformanceSessionResponse(s.getId(), s.getPerformanceTime());
        }
    }
}

