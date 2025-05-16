package com.pickgo.domain.post.review.controller;

import com.pickgo.domain.member.member.dto.MemberPrincipal;
import com.pickgo.domain.post.review.dto.PostReviewContentRequest;
import com.pickgo.domain.post.review.dto.PostReviewSimpleResponse;
import com.pickgo.domain.post.review.dto.PostReviewWithLikeResponse;
import com.pickgo.domain.post.review.service.PostReviewService;
import com.pickgo.global.response.RsCode;
import com.pickgo.global.response.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{id}/reviews")
@RequiredArgsConstructor
@Tag(name = "Review API", description = "리뷰 관련 API 엔드포인트")
public class PostReviewController {

    private final PostReviewService postReviewService;

    @Operation(
            summary = "게시글 리뷰 목록 조회",
            description = "특정 게시글 ID에 해당하는 리뷰 목록을 조회합니다."
    )
    @GetMapping
    public RsData<List<PostReviewWithLikeResponse>> getReviews(
            @PathVariable Long id,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(required = false) Integer cursorLikeCount,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        List<PostReviewWithLikeResponse> reviews = postReviewService.getReviewsByPostId(
                id,
                cursorId == null ? Long.MAX_VALUE : cursorId,
                cursorLikeCount == null ? Integer.MAX_VALUE : cursorLikeCount,
                size,
                sort,
                authHeader
        );
        return RsData.from(RsCode.SUCCESS, reviews);
    }

    @Operation(
            summary = "게시글 리뷰 작성",
            description = "게시글에 새로운 리뷰를 작성합니다."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RsData<PostReviewSimpleResponse> createReview(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal MemberPrincipal principal,
            @RequestBody PostReviewContentRequest request
    ) {
        PostReviewSimpleResponse response = postReviewService.createReview(id, principal.id(), request.content());
        return RsData.from(RsCode.REVIEW_CREATED, response);
    }

    @Operation(
            summary = "게시글 리뷰 수정",
            description = "특정 게시글의 특정 리뷰를 수정합니다."
    )
    @PutMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.OK)
    public RsData<PostReviewSimpleResponse> updateReview(
            @PathVariable("id") Long id,
            @PathVariable("reviewId") Long reviewId,
            @RequestBody PostReviewContentRequest request,
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        PostReviewSimpleResponse response = postReviewService.updateReview(id, reviewId, request.content(), principal.id());
        return RsData.from(RsCode.REVIEW_UPDATED, response);
    }

    @Operation(
            summary = "게시글 리뷰 삭제",
            description = "특정 게시글의 특정 리뷰를 삭제합니다."
    )
    @DeleteMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.OK)
    public RsData<String> deleteReview(
            @PathVariable("id") Long id,
            @PathVariable("reviewId") Long reviewId,
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        postReviewService.deleteReview(id, reviewId, principal.id());
        return RsData.from(RsCode.REVIEW_DELETED, null);
    }

    @Operation(summary = "리뷰 좋아요")
    @PostMapping("/{reviewId}/like")
    @ResponseStatus(HttpStatus.OK)
    public RsData<String> likeReview(
            @PathVariable("id") Long id,
            @PathVariable("reviewId") Long reviewId,
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        postReviewService.likeReview(id, reviewId, principal.id());
        return RsData.from(RsCode.SUCCESS, "리뷰에 좋아요를 추가했습니다.");
    }

    @Operation(summary = "리뷰 좋아요 취소")
    @DeleteMapping("/{reviewId}/like")
    @ResponseStatus(HttpStatus.OK)
    public RsData<String> unlikeReview(
            @PathVariable("id") Long id,
            @PathVariable("reviewId") Long reviewId,
            @AuthenticationPrincipal MemberPrincipal principal
    ) {
        postReviewService.cancelLikeReview(id, reviewId, principal.id());
        return RsData.from(RsCode.SUCCESS, "리뷰 좋아요를 취소했습니다.");
    }
}
