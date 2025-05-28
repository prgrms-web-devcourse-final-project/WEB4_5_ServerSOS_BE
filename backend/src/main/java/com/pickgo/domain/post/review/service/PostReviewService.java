package com.pickgo.domain.post.review.service;

import com.pickgo.domain.member.member.dto.MemberPrincipal;
import com.pickgo.domain.member.member.entity.Member;
import com.pickgo.domain.member.member.repository.MemberRepository;
import com.pickgo.domain.post.post.entity.Post;
import com.pickgo.domain.post.post.repository.PostRepository;
import com.pickgo.domain.post.review.dto.PostReviewSimpleResponse;
import com.pickgo.domain.post.review.dto.PostReviewWithLikeResponse;
import com.pickgo.domain.post.review.entity.Review;
import com.pickgo.domain.post.review.entity.ReviewLike;
import com.pickgo.domain.post.review.repository.PostReviewRepository;
import com.pickgo.domain.post.review.repository.ReviewLikeRepository;
import com.pickgo.global.exception.BusinessException;
import com.pickgo.global.jwt.JwtProvider;
import com.pickgo.global.response.RsCode;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.pickgo.global.response.RsCode.UNAUTHENTICATED;

@Service
@RequiredArgsConstructor
@Transactional
public class PostReviewService {

    private final PostReviewRepository postReviewRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final JwtProvider jwtProvider;
    private final RedissonClient redissonClient;

    // 게시글에 달린 리뷰 목록 조회

    /***
     * 리뷰 목록을 가져온 후, 현재 사용자가 좋아요를 눌렀는지 확인
     * @AuthenticationPrincipal을 사용하여 정보를 가져오려 했으나
     * 비회원인 경우 null이 들어와서 정상 동작하지 않는 문제 발생
     * 따라서, Authorization 헤더에서 Bearer Token을 직접 파싱하여 사용
     */
    @Transactional(readOnly = true)
    public List<PostReviewWithLikeResponse> getReviewsByPostId(
            Long postId,
            Long cursorId,
            int cursorLikeCount,
            int size,
            String sort,
            String authHeader
    ) {
        // 1. 리뷰 목록 조회
        List<Review> reviews = fetchReviews(postId, cursorId, cursorLikeCount, size, sort);

        // 2. 토큰 파싱 및 사용자 조회
        Member currentUser = extractMemberFromToken(authHeader);

        // 3. 좋아요 여부 확인 및 return
        return responseWithLikes(reviews, currentUser);
    }

    private List<Review> fetchReviews(Long postId, Long cursorId, int cursorLikeCount, int size, String sort) {
        PageRequest pageable = PageRequest.of(0, size);
        if ("like".equalsIgnoreCase(sort)) {
            return postReviewRepository.findByPostIdAndCursorLike(postId, cursorLikeCount, cursorId, pageable);
        } else {
            return postReviewRepository.findByPostIdAndCursorId(postId, cursorId, pageable);
        }
    }

    private Member extractMemberFromToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring("Bearer ".length());
                if (!jwtProvider.isValidToken(token)) {
                    throw new BusinessException(UNAUTHENTICATED);
                }
                Authentication auth = jwtProvider.getAuthentication(token);
                MemberPrincipal principal = (MemberPrincipal) auth.getPrincipal();
                return getMemberById(principal.id());
            } catch (Exception ignored) {
                return null;
            }
        }
        return null;
    }

    private List<PostReviewWithLikeResponse> responseWithLikes(List<Review> reviews, Member currentUser) {
        return reviews.stream()
                .map(r -> {
                    boolean liked = currentUser != null && reviewLikeRepository.existsByMemberAndReview(currentUser, r);
                    return PostReviewWithLikeResponse.fromEntity(r, liked);
                })
                .toList();
    }

    public PostReviewSimpleResponse createReview(Long id, UUID memberId, String content) {

        // 1. post 조회
        Post post = getPostById(id);

        // 2. member 조회
        Member member = getMemberById(memberId);

        // 3. 리뷰 생성
        Review review = Review.builder()
                .post(post)
                .member(member)
                .content(content)
                .build();

        Review savedReview = postReviewRepository.save(review);

        // 4. 연관 관계 설정
        post.addReview(savedReview);

        // 5. 응답 반환
        return PostReviewSimpleResponse.fromEntity(savedReview);
    }

    public PostReviewSimpleResponse updateReview(Long postId, Long reviewId, String content, UUID memberId) {
        // 1. 리뷰 조회 및 게시글 ID 일치 여부 검증
        Review review = getReviewByIdAndValidatePost(reviewId, postId);

        // 2. 유저 본인인지 검증
        Member actor = getMemberById(memberId);
        review.canAccess(actor);

        // 3. 내용 수정
        review.setContent(content);

        return PostReviewSimpleResponse.fromEntity(review);
    }

    /*
     리뷰 삭제
     */
    public void deleteReview(Long postId, Long reviewId, UUID memberId) {
        Review review = getReviewByIdAndValidatePost(reviewId, postId);

        // 2. 유저 본인인지 검증
        Member actor = getMemberById(memberId);
        review.canAccess(actor);

        postReviewRepository.delete(review);
    }

    public void likeReview(Long postId, Long reviewId, UUID memberId) {
        String lockKey = "review-like:" + reviewId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (lock.tryLock(3, 1, TimeUnit.SECONDS)) {
                Review review = getReviewByIdAndValidatePost(reviewId, postId);
                Member member = getMemberById(memberId);

                try {
                    reviewLikeRepository.save(
                            ReviewLike.builder().review(review).member(member).build()
                    );
                    review.incrementLikeCount();
                    postReviewRepository.flush();
                } catch (DataIntegrityViolationException e) {
                    throw new BusinessException(RsCode.REVIEW_ALREADY_LIKED);
                }

            } else {
                throw new BusinessException(RsCode.BAD_REQUEST);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(RsCode.INTERNAL_SERVER);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }


    public void cancelLikeReview(Long postId, Long reviewId, UUID memberId) {
        String lockKey = "review-like:" + reviewId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (lock.tryLock(3, 1, TimeUnit.SECONDS)) {
                Review review = getReviewByIdAndValidatePost(reviewId, postId);
                Member member = getMemberById(memberId);

                ReviewLike like = reviewLikeRepository.findByMemberAndReview(member, review)
                        .orElseThrow(() -> new BusinessException(RsCode.REVIEW_NOT_LIKED_YET));

                reviewLikeRepository.delete(like);
                review.decrementLikeCount();
                postReviewRepository.flush();

            } else {
                throw new BusinessException(RsCode.BAD_REQUEST);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(RsCode.INTERNAL_SERVER);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
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
