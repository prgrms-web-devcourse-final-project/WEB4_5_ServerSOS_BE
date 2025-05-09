package com.pickgo.domain.post.review.service;

import com.pickgo.domain.member.dto.MemberPrincipal;
import com.pickgo.domain.member.entity.Member;
import com.pickgo.domain.member.repository.MemberRepository;
import com.pickgo.domain.post.post.entity.Post;
import com.pickgo.domain.post.post.repository.PostRepository;
import com.pickgo.domain.post.review.dto.PostReviewCreateRequest;
import com.pickgo.domain.post.review.dto.PostReviewSimpleResponse;
import com.pickgo.domain.post.review.dto.PostReviewUpdateRequest;
import com.pickgo.domain.post.review.dto.PostReviewWithLikeResponse;
import com.pickgo.domain.post.review.entity.Review;
import com.pickgo.domain.post.review.entity.ReviewLike;
import com.pickgo.domain.post.review.repository.PostReviewRepository;
import com.pickgo.domain.post.review.repository.ReviewLikeRepository;
import com.pickgo.global.exception.BusinessException;
import com.pickgo.global.jwt.JwtProvider;
import com.pickgo.global.response.RsCode;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostReviewService {

    private final PostReviewRepository postReviewRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final JwtProvider jwtProvider;

    // 게시글에 달린 리뷰 목록 조회
    @Transactional(readOnly = true)
    public List<PostReviewWithLikeResponse> getReviewsByPostId(
            Long postId,
            Long cursorId,
            int cursorLikeCount,
            int size,
            String sort,
            String authHeader
    ) {
        PageRequest pageable = PageRequest.of(0, size);
        List<Review> reviews;

        if ("like".equalsIgnoreCase(sort)) {
            reviews = postReviewRepository.findByPostIdAndCursorLike(postId, cursorLikeCount, cursorId, pageable);
        } else {
            reviews = postReviewRepository.findByPostIdAndCursorId(postId, cursorId, pageable);
        }

        // 리뷰 목록을 가져온 후, 현재 사용자가 좋아요를 눌렀는지 확인
        // @AuthenticationPrincipal을 사용하여 정보를 가져오려 했으나
        // 비회원인 경우 null이 들어와서 정상 동작하지 않는 문제 발생
        // 따라서, Authorization 헤더에서 Bearer Token을 직접 파싱하여 사용
        Member currentUser = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring("Bearer ".length());
                jwtProvider.validateToken(token);
                Authentication auth = jwtProvider.getAuthentication(token);
                MemberPrincipal principal = (MemberPrincipal) auth.getPrincipal();
                currentUser = memberRepository.findById(principal.id())
                        .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
            } catch (Exception ignored) {
                // 무효한 토큰이면 무시하고 비회원 처리
            }
        }

        Member finalCurrentUser = currentUser;
        return reviews.stream()
                .map(r -> {
                    boolean liked = false;
                    if (finalCurrentUser != null) {
                        liked = reviewLikeRepository.existsByMemberAndReview(finalCurrentUser, r);
                    }
                    return PostReviewWithLikeResponse.fromEntity(r, liked);
                })
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
    public void likeReview(Long postId, Long reviewId, UUID memberId) {
        Review review = postReviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다."));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        isEqualId(review, postId);

        if (reviewLikeRepository.existsByMemberAndReview(member, review)) {
            throw new BusinessException(RsCode.REVIEW_ALREADY_LIKED);
        }

        review.incrementLikeCount();
        reviewLikeRepository.save(ReviewLike.builder()
                .review(review)
                .member(member)
                .build());
    }


    @Transactional
    public void cancelLikeReview(Long postId, Long reviewId, UUID memberId) {
        Review review = postReviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다."));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        isEqualId(review, postId);

        ReviewLike like = reviewLikeRepository.findByMemberAndReview(member, review)
                .orElseThrow(() -> new BusinessException(RsCode.REVIEW_NOT_LIKED_YET));

        review.decrementLikeCount();
        reviewLikeRepository.delete(like);
    }

    public void isEqualId(Review review, Long postId) {
        if (!review.getPost().getId().equals(postId)) {
            throw new BusinessException(RsCode.REVIEW_NOT_BELONG_TO_POST);
        }
    }

}
