package com.pickgo.domain.admin.repository;

import com.pickgo.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminPostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p JOIN FETCH p.performance WHERE p.id = :id")
    Optional<Post> findByIdWithPerformance(@Param("id") Long id);

    List<Post> findByIsPublished(Boolean isPublished);
}
