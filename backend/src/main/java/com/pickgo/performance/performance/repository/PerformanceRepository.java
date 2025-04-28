package com.pickgo.performance.performance.repository;

import com.pickgo.performance.performance.entity.Performance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceRepository extends JpaRepository<Performance, Long> {
}
