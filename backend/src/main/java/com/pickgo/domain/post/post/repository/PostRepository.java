package com.pickgo.domain.post.post.repository;

import com.pickgo.domain.performance.entity.PerformanceType;
import com.pickgo.domain.post.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("""
            SELECT p FROM Post p
            JOIN FETCH p.performance pf
            JOIN FETCH pf.venue v
            WHERE LOWER(REPLACE(p.title, ' ', '')) LIKE %:keyword%
            AND p.isPublished = true
            """)
    Page<Post> searchPostByTitle(Pageable pageable, String keyword);

    @Query("""
            SELECT p FROM Post p
            JOIN FETCH p.performance pf
            JOIN FETCH pf.venue v
            WHERE LOWER(REPLACE(p.title, ' ', '')) LIKE %:keyword% 
            AND pf.type = :type
            AND p.isPublished = true
            """)
    Page<Post> searchPostByTitleAndType(Pageable pageable, String keyword, PerformanceType type);

    @Query("""
                SELECT p FROM Post p
                JOIN FETCH p.performance pf
                JOIN FETCH pf.venue v
                WHERE p.isPublished = true
                ORDER BY p.views DESC
                LIMIT :size
            """)
    List<Post> findTopPostsByViews(int size);

    @Query("""
                SELECT p FROM Post p
                JOIN FETCH p.performance pf
                JOIN FETCH pf.venue v
                WHERE pf.type = :type
                AND p.isPublished = true
                ORDER BY p.views DESC
                LIMIT :size
            """)
    List<Post> findTopPostsByViewsAndType(int size, PerformanceType type);

    @Query("""
                SELECT p FROM Post p
                JOIN FETCH p.performance pf
                JOIN FETCH pf.venue v
                WHERE pf.state = 'SCHEDULED'
                AND p.isPublished = true
                ORDER BY pf.startDate ASC
                LIMIT 5
            """)
    List<Post> findTopScheduledPosts();

    @Query("""
                SELECT DISTINCT p FROM Post p
                JOIN FETCH p.performance perf
                LEFT JOIN FETCH perf.venue v
                LEFT JOIN FETCH perf.performanceSessions ps
                WHERE p.id = :id
            """)
    Optional<Post> findByIdWithAll(Long id);
}
