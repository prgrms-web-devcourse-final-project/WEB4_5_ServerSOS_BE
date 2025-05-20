package com.pickgo.domain.performance.area.seat.event;

import com.pickgo.domain.performance.area.seat.dto.SeatUpdateResponse;
import com.pickgo.domain.performance.area.seat.entity.ReservedSeat;
import com.pickgo.domain.performance.area.seat.service.SeatNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeatStatusChangedListener {

    private final SeatNotificationService seatNotificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleSeatStatusChangedEvent(SeatStatusChangedEvent event) {
        try {
            ReservedSeat seat = event.getSeat();
            Long sessionId = seat.getPerformanceSession().getId(); // 연관관계 필수

            seatNotificationService.notifySeatUpdate(sessionId, SeatUpdateResponse.from(seat));
        } catch (Exception e) {
            log.warn("좌석 상태 변경 이벤트 처리 중 예외 발생", e);
        }
    }
}
