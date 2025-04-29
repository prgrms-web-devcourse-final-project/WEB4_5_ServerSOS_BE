package com.pickgo.example.dto;

import com.pickgo.example.entity.Review;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostReviewSimpleResponse {

    private Long reviewId;
    private Long userId;
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
