package com.pickgo.domain.post.review.dto;

import com.pickgo.domain.post.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class PostReviewWithLikeResponse {

    private Long reviewId;
    private UUID userId;
    private String profile;
    private String nickname;
    private String content;
    private int likeCount;
    private boolean likedByCurrentUser;

    public static PostReviewWithLikeResponse fromEntity(Review review, boolean likedByCurrentUser) {
        return PostReviewWithLikeResponse.builder()
                .reviewId(review.getId())
                .userId(review.getMember().getId())
                .profile(review.getMember().getProfile())
                .nickname(review.getMember().getNickname())
                .content(review.getContent())
                .likeCount(review.getLikeCount())
                .likedByCurrentUser(likedByCurrentUser)
                .build();
    }
}
