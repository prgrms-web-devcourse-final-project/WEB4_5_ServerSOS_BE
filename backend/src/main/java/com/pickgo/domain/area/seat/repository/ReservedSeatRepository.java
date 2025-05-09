package com.pickgo.domain.area.seat.repository;

import com.pickgo.domain.area.area.entity.PerformanceArea;
import com.pickgo.domain.area.seat.entity.ReservedSeat;
import com.pickgo.domain.performance.entity.PerformanceSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservedSeatRepository extends JpaRepository<ReservedSeat, Long> {
    List<ReservedSeat> findByPerformanceAreaAndPerformanceSession(PerformanceArea area, PerformanceSession performanceSession);

    // 좌석 목록 조회
    List<ReservedSeat> findByPerformanceAreaIdAndPerformanceSessionId(Long areaId, Long sessionId);

    //좌석 상태 변경
    Optional<ReservedSeat> findByPerformanceSessionIdAndPerformanceAreaIdAndRowAndNumber(
            Long sessionId, Long areaId, String row, Integer number);
}
