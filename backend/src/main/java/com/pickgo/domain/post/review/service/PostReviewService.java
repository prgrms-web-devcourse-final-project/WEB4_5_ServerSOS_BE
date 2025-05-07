package com.pickgo.domain.post.review.service;

import com.pickgo.domain.member.entity.Member;
import com.pickgo.domain.member.repository.MemberRepository;
import com.pickgo.domain.post.review.dto.PostReviewCreateRequest;
import com.pickgo.domain.post.review.dto.PostReviewSimpleResponse;
import com.pickgo.domain.post.review.dto.PostReviewUpdateRequest;
import com.pickgo.domain.post.post.entity.Post;
import com.pickgo.domain.post.review.entity.Review;
import com.pickgo.domain.post.post.repository.PostRepository;
import com.pickgo.domain.post.review.repository.PostReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostReviewService {

    private final PostReviewRepository postreviewRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    // 게시글에 달린 리뷰 목록 조회
    public List<PostReviewSimpleResponse> getReviewsByPostId(Long postId) {
        List<Review> reviews = postreviewRepository.findAllByPostId(postId);
        return reviews.stream()
                .map(PostReviewSimpleResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public PostReviewSimpleResponse createReview(Long id, PostReviewCreateRequest request) {

        // 1. post 조회
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        // 2. member 조회
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 3. 리뷰 생성
        Review review = Review.builder()
                .post(post)
                .member(member)
                .content(request.getContent())
                .build();

        Review savedReview = postreviewRepository.save(review);

        // 4. 응답 반환
        return PostReviewSimpleResponse.fromEntity(savedReview);
    }

    @Transactional
    public PostReviewSimpleResponse updateReview(Long postId, Long reviewId, PostReviewUpdateRequest request) {
        Review review = postreviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다."));

        if (!review.getPost().getId().equals(postId)) {
            throw new IllegalArgumentException("리뷰가 해당 게시글에 속해있지 않습니다.");
        }

        review.setContent(request.getContent());

        return PostReviewSimpleResponse.fromEntity(review);
    }

    @Transactional
    public void deleteReview(Long postId, Long reviewId) {
        Review review = postreviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다."));

        if (!review.getPost().getId().equals(postId)) {
            throw new IllegalArgumentException("리뷰가 해당 게시글에 속하지 않습니다.");
        }

        postreviewRepository.delete(review);
    }
}
