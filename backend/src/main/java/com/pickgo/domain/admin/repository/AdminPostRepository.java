package com.pickgo.domain.admin.repository;

import com.pickgo.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminPostRepository extends JpaRepository<Post, Long> {


    @Query("SELECT p FROM Post p JOIN FETCH p.performance perf JOIN FETCH perf.venue")
    Page<Post> findAllWithPerformanceAndVenue(Pageable pageable);

    @Query("SELECT p FROM Post p JOIN FETCH p.performance perf JOIN FETCH perf.venue WHERE p.isPublished = :isPublished")
    Page<Post> findAllWithPerformanceAndVenueByIsPublished(@Param("isPublished") Boolean isPublished, Pageable pageable);


    Page<Post> findByIsPublished(Boolean isPublished, Pageable pageable);
}
