package com.pickgo.domain.admin.repository;

import com.pickgo.domain.post.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminPostRepository extends JpaRepository<Post, Long> {


    @EntityGraph(attributePaths = {
            "performance",
            "performance.venue"
    })
    Page<Post> findAllBy(Pageable pageable);

    @EntityGraph(attributePaths = {
            "performance",
            "performance.venue"
    })
    Page<Post> findAllByIsPublished(Boolean isPublished, Pageable pageable);

    @EntityGraph(attributePaths = {
            "performance",
            "performance.performanceAreas",
            "performance.performanceSessions",
            "performance.performanceIntros",
            "performance.venue"
    })
    @Query("SELECT p FROM Post p WHERE p.id = :id")
    Optional<Post> findById(@Param("id") Long id);

}
