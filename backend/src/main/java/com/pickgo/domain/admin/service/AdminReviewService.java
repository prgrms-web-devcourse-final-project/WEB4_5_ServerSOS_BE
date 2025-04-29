package com.pickgo.domain.admin.service;

import com.pickgo.domain.admin.dto.PostReviewCreateRequest;
import com.pickgo.domain.admin.dto.PostReviewSimpleResponse;
import com.pickgo.domain.member.entity.Member;
import com.pickgo.domain.post.entity.Post;
import com.pickgo.domain.review.entity.Review;
import com.pickgo.domain.member.repository.MemberRepository;
import com.pickgo.domain.post.repository.PostRepository;
import com.pickgo.domain.admin.repository.AdminReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminReviewService {

    private final AdminReviewRepository adminreviewRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    // 게시글에 달린 리뷰 목록 조회
    public List<PostReviewSimpleResponse> getReviewsByPostId(Long postId) {
        List<Review> reviews = adminreviewRepository.findAllByPostIdWithMember(postId);
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
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();

       Review savedReview = adminreviewRepository.save(review);

        // 4. 응답 반환
        return PostReviewSimpleResponse.fromEntity(savedReview);
    }
}
