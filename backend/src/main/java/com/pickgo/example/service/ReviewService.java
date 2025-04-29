package com.pickgo.example.service;

import com.pickgo.example.dto.PostReviewSimpleResponse;
import com.pickgo.example.entity.Review;
import com.pickgo.example.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    // 게시글에 달린 리뷰 목록 조회
    public List<PostReviewSimpleResponse> getReviewsByPostId(Long postId) {
        List<Review> reviews = reviewRepository.findByPostId(postId);
        return reviews.stream()
                .map(PostReviewSimpleResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
