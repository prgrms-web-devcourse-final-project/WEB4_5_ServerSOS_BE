package com.pickgo.domain.admin.dto;

import com.pickgo.domain.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class PostSimpleResponse {
    private Long id;
    private String title;
    private String venue;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String poster;

    public static PostSimpleResponse from(Post post){
        return PostSimpleResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .venue(post.getPerformance().getVenueId().getName())
                .startDate(post.getPerformance().getEndDate())
                .poster(post.getPerformance().getPoster())
                .build();
    }
}
