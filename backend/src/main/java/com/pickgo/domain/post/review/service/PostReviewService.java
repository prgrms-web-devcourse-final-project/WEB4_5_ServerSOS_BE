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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostReviewService {

    private final PostReviewRepository postReviewRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    // 리뷰 작성(생성)
    public PostReviewSimpleResponse createReview(Long postId, PostReviewCreateRequest request) {
       //Post 조회
        Post post = getPostById(postId);
        //Member  조회
        Member member = getMemberById(request.getMemberId());
        //리뷰 생성
        Review review = Review.builder()
                .post(post)
                .member(member)
                .content(request.getContent())
                .build();

        Review savedReview = postReviewRepository.save(review);
        //응답 반환
        return PostReviewSimpleResponse.fromEntity(savedReview);
    }

    // 리뷰 조회
    public List<PostReviewSimpleResponse> getReviewsByPostId(Long postId) {
        return postReviewRepository.findAllByPostId(postId).stream()
                .map(PostReviewSimpleResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // 리뷰 수정
    @Transactional
    public PostReviewSimpleResponse updateReview(Long postId, Long reviewId, PostReviewUpdateRequest request) {
        Review review = getReviewByIdAndValidatePost(reviewId, postId);
        review.setContent(request.getContent());
        return PostReviewSimpleResponse.fromEntity(review);
    }

    // 리뷰 삭제
    @Transactional
    public void deleteReview(Long postId, Long reviewId) {
        Review review = getReviewByIdAndValidatePost(reviewId, postId);
        postReviewRepository.delete(review);
    }


    // 예외처리 메서드
    private Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(RsCode.POST_NOT_FOUND));
    }

    private Member getMemberById(UUID memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(RsCode.MEMBER_NOT_FOUND));
    }

    private Review getReviewByIdAndValidatePost(Long reviewId, Long postId) {
        Review review = postReviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(RsCode.REVIEW_NOT_FOUND));

        if (!review.getPost().getId().equals(postId)) {
            throw new BusinessException(RsCode.REVIEW_NOT_FOUND);
        }

        return review;
    }
}
