package com.pickgo.domain.performance.area.area.service;

import com.pickgo.domain.performance.area.area.dto.PerformanceAreaDetailResponse;
import com.pickgo.domain.performance.area.area.entity.PerformanceArea;
import com.pickgo.domain.performance.area.area.repository.PerformanceAreaRepository;
import com.pickgo.domain.performance.area.seat.entity.ReservedSeat;
import com.pickgo.domain.performance.area.seat.repository.ReservedSeatRepository;
import com.pickgo.domain.performance.performance.entity.Performance;
import com.pickgo.domain.performance.performance.entity.PerformanceSession;
import com.pickgo.domain.performance.performance.repository.PerformanceSessionRepository;
import com.pickgo.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.pickgo.global.response.RsCode.PERFORMANCE_SESSION_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PerformanceAreaService {
    private final PerformanceSessionRepository performanceSessionRepository;
    private final PerformanceAreaRepository performanceAreaRepository;
    private final ReservedSeatRepository reservedSeatRepository;

    @Transactional(readOnly = true)
    public List<PerformanceAreaDetailResponse> getAreas(Long sessionId) {
        PerformanceSession performanceSession = performanceSessionRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException(PERFORMANCE_SESSION_NOT_FOUND));
        Performance performance = performanceSession.getPerformance();

        List<PerformanceArea> areas = performanceAreaRepository.findByPerformance(performance);

        List<PerformanceAreaDetailResponse> response = new ArrayList<>();
        for (PerformanceArea area : areas) {
            List<ReservedSeat> seats = reservedSeatRepository.findByPerformanceAreaAndPerformanceSession(area, performanceSession);
            response.add(PerformanceAreaDetailResponse.from(area, seats));
        }

        return response;
    }
}
