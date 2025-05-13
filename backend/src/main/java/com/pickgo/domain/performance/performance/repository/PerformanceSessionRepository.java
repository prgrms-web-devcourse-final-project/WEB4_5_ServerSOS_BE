package com.pickgo.domain.performance.performance.repository;

import com.pickgo.domain.performance.performance.entity.PerformanceSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceSessionRepository extends JpaRepository<PerformanceSession, Long> {
}
