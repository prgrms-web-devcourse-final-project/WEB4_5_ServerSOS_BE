package com.pickgo.domain.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pickgo.domain.area.area.entity.PerformanceArea;
import com.pickgo.domain.performance.entity.Performance;
import com.pickgo.domain.performance.entity.PerformanceIntro;
import com.pickgo.domain.performance.entity.PerformanceSession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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
    private int runtime;
    private int minAge;
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
                // 기본값 설정: state가 null이면 "공연예정"
                p.getState() != null ? p.getState().name() : "공연예정",

                // 기본값 설정: type이 null이면 "기타"
                p.getType() != null ? p.getType().name() : "기타",

                p.getCasts(),
                p.getRuntime() != null ? Integer.parseInt(p.getRuntime()) : 0,
                p.getMinAge() != null ? Integer.parseInt(p.getMinAge()) : 0,

                p.getStartDate() != null ? p.getStartDate().atStartOfDay() : null,
                p.getEndDate() != null ? p.getEndDate().atStartOfDay() : null,

                p.getCreatedAt(),
                p.getModifiedAt(),

                new VenueResponse(p.getVenue().getName(), p.getVenue().getAddress()),

                p.getPerformanceIntros() != null ?
                        p.getPerformanceIntros().stream()
                                .map(PerformanceIntro::getIntro_image)
                                .toList() : List.of(),

                p.getPerformanceAreas() != null ?
                        p.getPerformanceAreas().stream()
                                .map(PerformanceAreaResponse::from)
                                .toList() : List.of(),

                p.getPerformanceSessions() != null ?
                        p.getPerformanceSessions().stream()
                                .map(PerformanceSessionResponse::from)
                                .toList() : List.of()
        );
    }

    public record VenueResponse(String name, String address) {}

    public record PerformanceAreaResponse(Long id, String name, String grade, int price) {
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

