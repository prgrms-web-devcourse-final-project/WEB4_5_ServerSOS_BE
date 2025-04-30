package com.pickgo.domain.post.dto;

import com.pickgo.domain.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class PostReviewSimpleResponse {

    private Long reviewId;
    private UUID userId;
    private String profile;
    private String nickname;
    private String content;

    public static PostReviewSimpleResponse fromEntity(Review review) {
        return PostReviewSimpleResponse.builder()
                .reviewId(review.getId())
                .userId(review.getMember().getId())
                .profile(review.getMember().getProfile())
                .nickname(review.getMember().getNickname())
                .content(review.getContent())
                .build();
    }
}
