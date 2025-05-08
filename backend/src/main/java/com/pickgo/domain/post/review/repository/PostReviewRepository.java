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
    // 최신순 정렬 (id DESC)
    @Query("SELECT r FROM Review r WHERE r.post.id = :postId AND r.id < :cursorId ORDER BY r.id DESC")
    List<Review> findByPostIdAndCursorId(Long postId, Long cursorId, Pageable pageable);

    // 좋아요 + 최신순 정렬
    @Query("""
                SELECT r FROM Review r 
                WHERE r.post.id = :postId 
                  AND (r.likeCount < :cursorLikeCount 
                       OR (r.likeCount = :cursorLikeCount AND r.id < :cursorId))
                ORDER BY r.likeCount DESC, r.id DESC
            """)
    List<Review> findByPostIdAndCursorLike(Long postId, int cursorLikeCount, Long cursorId, Pageable pageable);

}