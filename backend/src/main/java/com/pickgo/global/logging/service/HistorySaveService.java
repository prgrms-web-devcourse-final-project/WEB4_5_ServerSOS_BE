package com.pickgo.global.logging.service;


import com.pickgo.domain.log.entity.ReservationHistory;
import com.pickgo.domain.log.enums.ActionType;
import com.pickgo.domain.log.repository.ReservationHistoryRepository;
import com.pickgo.domain.reservation.entity.Reservation;
import com.pickgo.global.logging.dto.LogContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HistorySaveService {

    private final ReservationHistoryRepository reservationHistoryRepository;

    /***
     * 로깅에서 오류가 나도 메소드가 정상 작동할 수 있도록 별도 트랜잭션 생성
     * 예약 로그 저장
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveReservationHistory(Reservation reservation, LogContext logContext, ActionType actionType) {
        ReservationHistory history = new ReservationHistory(
                reservation.getId(),
                reservation.getStatus(),
                reservation.getTotalPrice(),
                reservation.getCreatedAt(),
                reservation.getPerformanceSession().getId(),
                logContext.actorId(),
                logContext.actorType(),
                actionType,
                null,
                null,
                logContext.requestUri(),
                logContext.httpMethod(),
                "예약 : " + actionType.getDescription()
        );

        reservationHistoryRepository.save(history);
    }
}
