package com.pickgo.example.repository;

import com.pickgo.example.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review>findByPostId(Long postId);
}
