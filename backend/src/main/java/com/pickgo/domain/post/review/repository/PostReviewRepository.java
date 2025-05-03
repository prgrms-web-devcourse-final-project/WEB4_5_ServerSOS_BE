package com.pickgo.domain.post.review.repository;

import com.pickgo.domain.post.review.entity.Review;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostReviewRepository extends JpaRepository<Review, Long> {

    @EntityGraph(attributePaths = {"member"})
    List<Review> findAllByPostId(Long postId);
}