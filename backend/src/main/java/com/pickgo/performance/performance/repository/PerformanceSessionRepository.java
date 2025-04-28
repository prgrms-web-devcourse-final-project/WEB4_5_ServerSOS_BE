package com.pickgo.performance.performance.repository;

import com.pickgo.performance.performance.entity.PerformanceSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceSessionRepository extends JpaRepository<PerformanceSession, Long> {
}
