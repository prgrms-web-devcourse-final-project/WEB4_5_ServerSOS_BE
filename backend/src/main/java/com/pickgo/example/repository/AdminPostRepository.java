package com.pickgo.example.repository;

import com.pickgo.example.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminPostRepository extends JpaRepository<Post, Long> {
    @Query("""
    SELECT p FROM Post p
    JOIN FETCH p.performance perf
    JOIN FETCH perf.venueId
    """)
    List<Post> findAllWithPerformance();
}
