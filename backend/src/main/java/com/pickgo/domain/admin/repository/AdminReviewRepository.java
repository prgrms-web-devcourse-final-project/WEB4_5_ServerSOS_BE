package com.pickgo.domain.admin.repository;

import com.pickgo.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AdminReviewRepository extends JpaRepository<Review, Long> {

    List<Review>findByPostId(Long postId);

    @Query("""
        SELECT r
        FROM Review r
        JOIN FETCH r.member
        WHERE r.post.id = :postId
    """)
    List<Review> findAllByPostIdWithMember(@Param("postId") Long postId);
}
