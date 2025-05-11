package com.pickgo.domain.area.area.repository;

import com.pickgo.domain.area.area.entity.PerformanceArea;
import com.pickgo.domain.performance.entity.Performance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PerformanceAreaRepository extends JpaRepository<PerformanceArea, Long> {
    List<PerformanceArea> findByPerformance(Performance performance);
}
