package com.pickgo.domain.post.controller;

import com.pickgo.domain.post.dto.PostReviewCreateRequest;
import com.pickgo.domain.post.dto.PostReviewSimpleResponse;
import com.pickgo.domain.post.dto.PostReviewUpdateRequest;
import com.pickgo.domain.post.service.PostReviewService;
import com.pickgo.global.response.RsCode;
import com.pickgo.global.response.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{id}/reviews")
@RequiredArgsConstructor
@Tag(name = "Admin API", description = " 리뷰 관련 API 엔드포인트")
public class PostReviewController {

    private final PostReviewService postReviewService;

    @Operation(summary = "게시글 리뷰 목록 조회")
    @GetMapping
    public RsData<List<PostReviewSimpleResponse>> getReviews(
            @PathVariable Long id
    ) {
        List<PostReviewSimpleResponse> reviews = postReviewService.getReviewsByPostId(id);
        return RsData.from(RsCode.SUCCESS, reviews);
    }

    @Operation(summary = "게시글 리뷰 작성")
    @PostMapping
    public RsData<PostReviewSimpleResponse> createReview(
            @PathVariable Long id,
            @RequestBody PostReviewCreateRequest request
    ) {
        PostReviewSimpleResponse response = postReviewService.createReview(id, request);
        return RsData.from(RsCode.CREATED, response);
    }

    @Operation(summary = "게시글 리뷰 수정")
    @PutMapping("/{reviewId}")
    public RsData<PostReviewSimpleResponse> updateReview(
            @PathVariable Long id,
            @PathVariable Long reviewId,
            @RequestBody PostReviewUpdateRequest request
    ) {
        PostReviewSimpleResponse response = postReviewService.updateReview(id, reviewId, request);
        return new RsData<>(200, "리뷰 등록 완료",response);
    }

    @Operation(summary = "게시글 리뷰 삭제")
    @DeleteMapping("/{reviewId}")
    public RsData<String> deleteReview(
            @PathVariable Long id,
            @PathVariable Long reviewId
    ) {
        postReviewService.deleteReview(id, reviewId);
        return new RsData<>(200, "리뷰 삭제 완료", null);
    }
}
