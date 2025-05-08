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

    /*
     리뷰 작성(생성)
    request 리뷰 생성 요청 DTO
     */
    public PostReviewSimpleResponse createReview(Long postId, PostReviewCreateRequest request) {
       //게시글 존재 여부 확인
        Post post = getPostById(postId);
        //회원 존재 여부 확인
        Member member = getMemberById(request.getMemberId());
        //리뷰 엔티티 생성 및 저장
        Review review = Review.builder()
                .post(post)
                .member(member)
                .content(request.getContent())
                .build();

        Review savedReview = postReviewRepository.save(review);
        //응답 DTO로 반환
        return PostReviewSimpleResponse.fromEntity(savedReview);
    }

    /*
     리뷰 조회
     리뷰 응답 리스트
     */
    public List<PostReviewSimpleResponse> getReviewsByPostId(Long postId) {
        return postReviewRepository.findAllByPostId(postId).stream()
                .map(PostReviewSimpleResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /*
     리뷰 수정
     request 리뷰 수정 요청 DTO
     수정된 리뷰 응답 DTO 반환
     */
    @Transactional
    public PostReviewSimpleResponse updateReview(Long postId, Long reviewId, PostReviewUpdateRequest request) {
        //리뷰 조회 및 게시글 ID 일치 여부 검증
        Review review = getReviewByIdAndValidatePost(reviewId, postId);
       // 내용 수정
        review.setContent(request.getContent());
        return PostReviewSimpleResponse.fromEntity(review);
    }

    /*
     리뷰 삭제
     */
    @Transactional
    public void deleteReview(Long postId, Long reviewId) {
        Review review = getReviewByIdAndValidatePost(reviewId, postId);
        postReviewRepository.delete(review);
    }


    /*
     예외처리 메서드
     게시글 ID로 조회 ,예외처리
     */
    private Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(RsCode.POST_NOT_FOUND));
    }
    /*
    회원 ID로 조회 + 예외처리
     */
    private Member getMemberById(UUID memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(RsCode.MEMBER_NOT_FOUND));
    }
    /*
    리뷰 ID로 조회하고 + 해당하는 리뷰가 해당 게시글에 속해 있는지 검증 및 예외처리
     */
    private Review getReviewByIdAndValidatePost(Long reviewId, Long postId) {
        Review review = postReviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(RsCode.REVIEW_NOT_FOUND));

        if (!review.getPost().getId().equals(postId)) {
            throw new BusinessException(RsCode.REVIEW_NOT_FOUND);
        }

        return review;
    }
}
