package com.pickgo.example.controller;

import com.pickgo.example.dto.PostReviewSimpleResponse;
import com.pickgo.example.service.ReviewService;
import com.pickgo.global.response.RsCode;
import com.pickgo.global.response.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{id}/reviews")
@RequiredArgsConstructor
@Tag(name = "Admin API", description = " 리뷰 관련 API 엔드포인트")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "게시글 리뷰 목록 조회")
    @GetMapping
    public RsData<List<PostReviewSimpleResponse>> getReviews(
            @PathVariable Long id
    ) {
        List<PostReviewSimpleResponse> reviews = reviewService.getReviewsByPostId(id);
        return RsData.from(RsCode.SUCCESS,reviews);
    }
}
