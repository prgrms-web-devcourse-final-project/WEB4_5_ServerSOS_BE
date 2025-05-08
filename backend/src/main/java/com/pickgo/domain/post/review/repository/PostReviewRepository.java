package com.pickgo.domain.post.review.repository;

import com.pickgo.domain.post.review.entity.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostReviewRepository extends JpaRepository<Review, Long> {

    @EntityGraph(attributePaths = {"member"})
    List<Review> findAllByPostId(Long postId);

    // 게시글 리뷰 목록 조회 (커서 기반 페이징)
    @Query("SELECT r FROM Review r WHERE r.post.id = :postId AND r.id < :cursorId ORDER BY r.likeCount DESC")
    List<Review> findByPostIdAndCursorIdOrderByLikeCount(Long postId, Long cursorId, Pageable pageable);

}