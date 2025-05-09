package com.pickgo.domain.area.seat.service;

import com.pickgo.domain.area.seat.entity.ReservedSeat;
import com.pickgo.domain.area.seat.entity.SeatStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeatPublisher {

    private final SeatNotificationService seatNotificationService;

    public void sendSeatStatusUpdate(ReservedSeat seat) {
        Long sessionId = seat.getPerformanceSession().getId();

        // 클라이언트에 전달할 데이터 구조는 상황에 따라 DTO로 가공할 수도 있음
        SeatUpdateDto dto = new SeatUpdateDto(
                seat.getId(),
                seat.getPerformanceArea().getId(),
                seat.getRow(),
                seat.getNumber(),
                seat.getStatus()
        );

        seatNotificationService.notifySeatUpdate(sessionId, dto);
    }

    // 내부 전송용 DTO
    public record SeatUpdateDto(
            Long seatId,
            Long areaId,
            String row,
            Integer number,
            SeatStatus status

    ) {}
}
