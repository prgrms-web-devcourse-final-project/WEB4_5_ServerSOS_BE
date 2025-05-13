package com.pickgo.domain.performance.area.area.service;

import com.pickgo.domain.performance.area.area.dto.PerformanceAreaDetailResponse;
import com.pickgo.domain.performance.area.area.entity.AreaGrade;
import com.pickgo.domain.performance.area.area.entity.AreaName;
import com.pickgo.domain.performance.area.area.entity.PerformanceArea;
import com.pickgo.domain.performance.area.area.repository.PerformanceAreaRepository;
import com.pickgo.domain.performance.area.area.service.PerformanceAreaService;
import com.pickgo.domain.performance.area.seat.entity.ReservedSeat;
import com.pickgo.domain.performance.area.seat.entity.SeatStatus;
import com.pickgo.domain.performance.area.seat.repository.ReservedSeatRepository;
import com.pickgo.domain.performance.performance.entity.Performance;
import com.pickgo.domain.performance.performance.entity.PerformanceSession;
import com.pickgo.domain.performance.performance.repository.PerformanceSessionRepository;
import com.pickgo.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PerformanceAreaServiceTest {
    @Mock
    private PerformanceSessionRepository performanceSessionRepository;

    @Mock
    private PerformanceAreaRepository performanceAreaRepository;

    @Mock
    private ReservedSeatRepository reservedSeatRepository;

    @InjectMocks
    private PerformanceAreaService performanceAreaService;

    @Test
    @DisplayName("구역 조회 테스트")
    void getAreas() {
        // given
        Long sessionId = 1L;
        PerformanceSession session = mock(PerformanceSession.class);
        Performance performance = mock(Performance.class);

        PerformanceArea area1 = PerformanceArea.builder()
                .id(1L)
                .name(AreaName.VIP)
                .grade(AreaGrade.PREMIUM)
                .price(100000)
                .rowCount(5)
                .colCount(10)
                .performance(performance)
                .build();

        PerformanceArea area2 = PerformanceArea.builder()
                .id(2L)
                .name(AreaName.A)
                .grade(AreaGrade.ROYAL)
                .price(80000)
                .rowCount(6)
                .colCount(10)
                .performance(performance)
                .build();
        List<PerformanceArea> areas = List.of(area1, area2);

        ReservedSeat seat1 = ReservedSeat.builder()
                .performanceArea(area1)
                .row("A")
                .number(1)
                .status(SeatStatus.RESERVED)
                .build();

        ReservedSeat seat2 = ReservedSeat.builder()
                .performanceArea(area1)
                .row("A")
                .number(2)
                .status(SeatStatus.RESERVED)
                .build();

        given(performanceSessionRepository.findById(sessionId))
                .willReturn(Optional.of(session));
        given(session.getPerformance())
                .willReturn(performance);
        given(performanceAreaRepository.findByPerformance(performance))
                .willReturn(areas);
        given(reservedSeatRepository.findByPerformanceAreaAndPerformanceSession(any(), eq(session)))
                .willReturn(List.of(seat1, seat2));

        // when
        List<PerformanceAreaDetailResponse> result = performanceAreaService.getAreas(sessionId);

        // then
        assertThat(result).hasSize(2);
        verify(reservedSeatRepository, times(2))
                .findByPerformanceAreaAndPerformanceSession(any(), eq(session));
    }

    @Test
    @DisplayName("구역 조회 실패")
    void getAreas_performanceSessionNotFound_throwsException() {
        // given
        Long sessionId = 999999999L;
        given(performanceSessionRepository.findById(sessionId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> performanceAreaService.getAreas(sessionId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("존재하지 않는 공연 회차입니다.");
    }
}