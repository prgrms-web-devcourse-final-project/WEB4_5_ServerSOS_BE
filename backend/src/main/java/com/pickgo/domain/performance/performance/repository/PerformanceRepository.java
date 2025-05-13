package com.pickgo.domain.performance.performance.repository;

import com.pickgo.domain.performance.performance.entity.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface PerformanceRepository extends JpaRepository<Performance, Long> {
    boolean existsByNameAndPoster(String name, String poster);

    @Modifying
    @Query("""
            UPDATE Performance p SET p.state = 'COMPLETED'
            WHERE p.endDate < :today AND p.state <> 'COMPLETED'
            """)
    void UpdateToCompleted(LocalDate today);

    @Modifying
    @Query("""
            UPDATE Performance p SET p.state = 'ONGOING'
            WHERE p.startDate <= :today AND p.state = 'SCHEDULED'
            """)
    void UpdateToOngoing(LocalDate today);
}
