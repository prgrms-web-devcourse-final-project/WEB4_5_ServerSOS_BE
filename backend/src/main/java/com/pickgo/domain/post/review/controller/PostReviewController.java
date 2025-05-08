package com.pickgo.domain.post.review.controller;

import com.pickgo.domain.post.review.dto.PostReviewCreateRequest;
import com.pickgo.domain.post.review.dto.PostReviewSimpleResponse;
import com.pickgo.domain.post.review.dto.PostReviewUpdateRequest;
import com.pickgo.domain.post.review.service.PostReviewService;
import com.pickgo.global.response.RsCode;
import com.pickgo.global.response.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{id}/reviews")
@RequiredArgsConstructor
@Tag(name = "리뷰 API", description = "리뷰 관련 API 엔드포인트")
public class PostReviewController {

    private final PostReviewService postReviewService;

    @Operation(summary = "게시글 리뷰 목록 조회")
    @GetMapping
    public RsData<List<PostReviewSimpleResponse>> getReviews(
            @PathVariable Long id,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(required = false) Integer cursorLikeCount,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "latest") String sort
    ) {
        List<PostReviewSimpleResponse> reviews = postReviewService.getReviewsByPostId(
                id,
                cursorId == null ? Long.MAX_VALUE : cursorId,
                cursorLikeCount == null ? Integer.MAX_VALUE : cursorLikeCount,
                size,
                sort
        );
        return RsData.from(RsCode.SUCCESS, reviews);
    }

    @Operation(summary = "게시글 리뷰 작성")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RsData<PostReviewSimpleResponse> createReview(
            @PathVariable("id") Long id,
            @RequestBody PostReviewCreateRequest request
    ) {
        PostReviewSimpleResponse response = postReviewService.createReview(id, request);
        return RsData.from(RsCode.REVIEW_CREATED, response);
    }

    @Operation(summary = "게시글 리뷰 수정")
    @PutMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.OK)
    public RsData<PostReviewSimpleResponse> updateReview(
            @PathVariable("id") Long id,
            @PathVariable("reviewId") Long reviewId,
            @RequestBody PostReviewUpdateRequest request
    ) {
        PostReviewSimpleResponse response = postReviewService.updateReview(id, reviewId, request);
        return RsData.from(RsCode.REVIEW_UPDATED, response);
    }

    @Operation(summary = "게시글 리뷰 삭제")
    @DeleteMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.OK)
    public RsData<String> deleteReview(
            @PathVariable("id") Long id,
            @PathVariable("reviewId") Long reviewId
    ) {
        postReviewService.deleteReview(id, reviewId);
        return RsData.from(RsCode.REVIEW_DELETED, null);
    }

    @Operation(summary = "리뷰 좋아요")
    @PostMapping("/{reviewId}/like")
    @ResponseStatus(HttpStatus.OK)
    public RsData<String> likeReview(
            @PathVariable("id") Long id,
            @PathVariable("reviewId") Long reviewId
    ) {
        postReviewService.likeReview(id, reviewId);
        return RsData.from(RsCode.SUCCESS, "리뷰에 좋아요를 추가했습니다.");
    }

    @Operation(summary = "리뷰 좋아요 취소")
    @DeleteMapping("/{reviewId}/like")
    @ResponseStatus(HttpStatus.OK)
    public RsData<String> unlikeReview(
            @PathVariable("id") Long id,
            @PathVariable("reviewId") Long reviewId
    ) {
        postReviewService.cancelLikeReview(id, reviewId);
        return RsData.from(RsCode.SUCCESS, "리뷰 좋아요를 취소했습니다.");
    }
}
