package com.pickgo.domain.admin.dto;

import com.pickgo.domain.post.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Builder
public class PostSimpleResponse {
    private Long id;
    private String title;
    private String venue;
    private LocalDate startDate;
    private LocalDate endDate;
    private String poster;

    public static PostSimpleResponse from(Post post) {
        return PostSimpleResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .venue(post.getPerformance().getVenue().getName())
                .startDate(post.getPerformance().getEndDate())
                .poster(post.getPerformance().getPoster())
                .build();
    }
}
