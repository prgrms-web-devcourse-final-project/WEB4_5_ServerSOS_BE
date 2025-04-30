package com.pickgo.domain.admin.dto;

import com.pickgo.domain.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDetailResponse {

    private Long id;
    private String title;
    private String content;
    private Boolean isPublished;
    private Long views;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private String performanceName;

    public static PostDetailResponse from(Post post) {
        return PostDetailResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .isPublished(post.getIsPublished())
                .views(post.getViews())
                .createdAt(post.getCreatedAt())
                .modifiedAt(post.getModifiedAt())
                .performanceName(post.getPerformance().getName())
                .build();
    }
}
