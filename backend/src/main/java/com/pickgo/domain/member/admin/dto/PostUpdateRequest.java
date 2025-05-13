package com.pickgo.domain.member.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostUpdateRequest {
    private String title;
    private String content;
    private Boolean isPublished;
    private PerformanceUpdateRequest performance;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PerformanceUpdateRequest {
        private Long id;
        private String name;
        private String poster;
        private String overview;
        private String state;
        private String type;
        private String casts;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private Integer runtime;
        private Integer minAge;
        private VenueRequest venue;
        private List<AreaPriceUpdateRequest> areas;
        private List<String> introImages;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class VenueRequest {
            private String name;
            private String address;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class AreaPriceUpdateRequest {
            private Long id;
            private Integer price;
        }
    }
}
