package com.pickgo.example.dto;

import com.pickgo.example.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
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
