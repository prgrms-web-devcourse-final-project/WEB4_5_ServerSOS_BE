package com.pickgo.domain.post.dto;

import com.pickgo.domain.post.entity.Review;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostReviewCreateRequest {
    private Long reviewId;
    private String profile;
    private String nickname;
    private UUID memberId;
    private String content;

    public static PostReviewCreateRequest from(Review review) {
        return PostReviewCreateRequest.builder()
                .reviewId(review.getId())
                .memberId(review.getMember().getId())
                .profile(review.getMember().getProfile())
                .nickname(review.getMember().getNickname())
                .content(review.getContent())
                .build();
    }
}
