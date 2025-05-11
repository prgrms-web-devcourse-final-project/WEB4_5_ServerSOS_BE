package com.pickgo.domain.area.seat.event;

import com.pickgo.domain.area.seat.dto.SeatUpdateRequest;
import com.pickgo.domain.area.seat.entity.ReservedSeat;
import com.pickgo.domain.area.seat.service.SeatNotificationService;
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

        seatNotificationService.notifySeatUpdate(sessionId, SeatUpdateRequest.from(seat));
    }
}
