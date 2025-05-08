package com.pickgo.domain.post.review.service;

import com.pickgo.domain.member.entity.Member;
import com.pickgo.domain.member.repository.MemberRepository;
import com.pickgo.domain.post.post.entity.Post;
import com.pickgo.domain.post.post.repository.PostRepository;
import com.pickgo.domain.post.review.dto.PostReviewCreateRequest;
import com.pickgo.domain.post.review.dto.PostReviewSimpleResponse;
import com.pickgo.domain.post.review.dto.PostReviewUpdateRequest;
import com.pickgo.domain.post.review.entity.Review;
import com.pickgo.domain.post.review.repository.PostReviewRepository;
import com.pickgo.global.exception.BusinessException;
import com.pickgo.global.response.RsCode;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostReviewService {

    private final PostReviewRepository postReviewRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    // 게시글에 달린 리뷰 목록 조회
    public List<PostReviewSimpleResponse> getReviewsByPostId(Long postId, Long cursorId, int size) {
//        List<Review> reviews = postReviewRepository.findAllByPostId(postId);
//        return reviews.stream()
//                .map(PostReviewSimpleResponse::fromEntity)
//                .collect(Collectors.toList());
        PageRequest pageable = PageRequest.of(0, size);
        List<Review> reviews = postReviewRepository.findByPostIdAndCursorIdOrderByLikeCount(postId, cursorId, pageable);

        return reviews.stream()
                .map(PostReviewSimpleResponse::fromEntity)
                .toList();
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

        Review savedReview = postReviewRepository.save(review);

        // 4. 응답 반환
        return PostReviewSimpleResponse.fromEntity(savedReview);
    }

    @Transactional
    public PostReviewSimpleResponse updateReview(Long postId, Long reviewId, PostReviewUpdateRequest request) {
        Review review = postReviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다."));

        isEqualId(review, postId);
        review.setContent(request.getContent());

        return PostReviewSimpleResponse.fromEntity(review);
    }

    @Transactional
    public void deleteReview(Long postId, Long reviewId) {
        Review review = postReviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다."));

        isEqualId(review, postId); // 리뷰가 해당 게시글에 속해있는지 확인
        postReviewRepository.delete(review);
    }

    @Transactional
    public void likeReview(Long postId, Long reviewId) {
        Review review = postReviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다."));

        isEqualId(review, postId);
        review.incrementLikeCount();
    }

    @Transactional
    public void cancelLikeReview(Long postId, Long reviewId) {
        Review review = postReviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다."));

        isEqualId(review, postId);
        review.decrementLikeCount();
    }

    public void isEqualId(Review review, Long postId) {
        if (!review.getPost().getId().equals(postId)) {
            throw new BusinessException(RsCode.REVIEW_NOT_BELONG_TO_POST);
        }
    }

}
