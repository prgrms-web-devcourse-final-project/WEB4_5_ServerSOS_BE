package com.pickgo.domain.post.post.dto;

import com.pickgo.domain.performance.performance.dto.PerformanceDetailResponse;
import com.pickgo.domain.post.post.entity.Post;

import java.time.LocalDateTime;

public record PostDetailResponse(
        Long id,
        String title,
        String content,
        Boolean isPublished,
        Long views,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt,
        PerformanceDetailResponse performance
) {
    public static PostDetailResponse from(Post post) {
        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getIsPublished(),
                post.getViews(),
                post.getCreatedAt(),
                post.getModifiedAt(),
                PerformanceDetailResponse.from(post.getPerformance())
        );
    }
}
