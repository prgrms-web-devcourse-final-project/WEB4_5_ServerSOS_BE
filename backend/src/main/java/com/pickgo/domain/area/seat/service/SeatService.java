package com.pickgo.domain.area.seat.service;

import com.pickgo.domain.area.seat.dto.SeatResponse;
import com.pickgo.domain.area.seat.entity.Seat;
import com.pickgo.domain.area.seat.entity.SeatStatus;
import com.pickgo.domain.area.seat.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;
    private final SeatNotificationService seatNotificationService;

    public List<SeatResponse> getSeats(Long areaId, Long seesionId) {
        return seatRepository.findByPerformanceAreaIdAndPerformanceSessionId(areaId, seesionId)
                .stream()
                .map(SeatResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateSeatStatus(Long seatId, SeatStatus status) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow();
        seat.setStatus(status);
        seatRepository.save(seat);

        Long sessionId = seat.getPerformanceSession().getId();
        seatNotificationService.notifySeatUpdate(sessionId, SeatResponse.from(seat));
    }

    public SseEmitter subscribeToSeatUpdates(Long sessionId) {
        return seatNotificationService.subscribe(sessionId);
    }
}