package com.pickgo.domain.reservation.scheduler;

import com.pickgo.domain.reservation.entity.Reservation;
import com.pickgo.domain.reservation.enums.ReservationStatus;
import com.pickgo.domain.reservation.repository.ReservationRepository;
import com.pickgo.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationTimeoutScheduler {
    private final ReservationRepository reservationRepository;
    private final ReservationService reservationService;

    @Scheduled(fixedRate = 60_000) // 매 1분마다 실행
    public void cancelExpiredReservations() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(5);

        // 1. 5분 넘었고, RESERVED 상태인 것 조회
        List<Reservation> expired = reservationRepository
                .findByStatusAndCreatedAtBefore(ReservationStatus.RESERVED, threshold);

        // 2. 해당 예약을 취소
        for (Reservation reservation : expired) {
            reservationService.cancelReservation(reservation.getId());
        }
    }
}
