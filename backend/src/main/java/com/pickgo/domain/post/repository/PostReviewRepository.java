package com.pickgo.domain.post.repository;

import com.pickgo.domain.review.entity.Review;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByPostId(Long postId);

    @EntityGraph(attributePaths = {"member"})
    List<Review> findAllByPostId(Long postId);
}