package com.pickgo.domain.performance.repository;

import com.pickgo.domain.performance.entity.PerformanceSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceSessionRepository extends JpaRepository<PerformanceSession, Long> {
}
