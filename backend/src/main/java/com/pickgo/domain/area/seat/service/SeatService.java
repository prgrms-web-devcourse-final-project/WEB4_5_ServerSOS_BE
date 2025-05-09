package com.pickgo.domain.area.seat.service;

import com.pickgo.domain.area.seat.dto.SeatResponse;
import com.pickgo.domain.area.seat.dto.SeatUpdateRequest;
import com.pickgo.domain.area.seat.entity.ReservedSeat;
import com.pickgo.domain.area.seat.repository.ReservedSeatRepository;
import com.pickgo.global.exception.BusinessException;
import com.pickgo.global.response.RsCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final ReservedSeatRepository reservedSeatRepository;
    private final SeatNotificationService seatNotificationService;

    public List<SeatResponse> getSeats(Long areaId, Long seesionId) {
        return reservedSeatRepository.findByPerformanceAreaIdAndPerformanceSessionId(areaId, seesionId)
                .stream()
                .map(SeatResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateSeatStatus(SeatUpdateRequest request) {
        ReservedSeat seat = reservedSeatRepository.findByPerformanceSessionIdAndPerformanceAreaIdAndRowAndNumber(
                request.sessionId(),
                request.areaId(),
                request.row(),
                request.number()
        )
                .orElseThrow(()-> new BusinessException(RsCode.INVALID_SEAT_POSITION));
        seat.setStatus(request.status());
        reservedSeatRepository.save(seat);


        seatNotificationService.notifySeatUpdate(request.sessionId(), SeatResponse.from(seat));
    }

    public SseEmitter subscribeToSeatUpdates(Long sessionId) {
        return seatNotificationService.subscribe(sessionId);
    }
}