package com.pickgo.domain.performance.area.seat.event;

import com.pickgo.domain.performance.area.seat.dto.SeatUpdateResponse;
import com.pickgo.domain.performance.area.seat.entity.ReservedSeat;
import com.pickgo.domain.performance.area.seat.service.SeatNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeatStatusChangedListener {

    private final SeatNotificationService seatNotificationService;

    @EventListener
    public void handleSeatStatusChangedEvent(SeatStatusChangedEvent event) {
        ReservedSeat seat = event.getSeat();
        Long sessionId = seat.getPerformanceSession().getId(); // 연관관계 필수

        seatNotificationService.notifySeatUpdate(sessionId, SeatUpdateResponse.from(seat));
    }
}
