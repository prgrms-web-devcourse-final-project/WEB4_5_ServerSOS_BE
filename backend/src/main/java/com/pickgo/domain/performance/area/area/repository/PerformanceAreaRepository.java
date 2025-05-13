package com.pickgo.domain.performance.area.area.repository;

import com.pickgo.domain.performance.area.area.entity.PerformanceArea;
import com.pickgo.domain.performance.performance.entity.Performance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PerformanceAreaRepository extends JpaRepository<PerformanceArea, Long> {
    List<PerformanceArea> findByPerformance(Performance performance);
}
