package com.pickgo.domain.reservation.scheduler;

import com.pickgo.domain.reservation.entity.Reservation;
import com.pickgo.domain.reservation.enums.ReservationStatus;
import com.pickgo.domain.reservation.repository.ReservationRepository;
import com.pickgo.domain.reservation.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Profile("test")
public class TestReservationTimeoutScheduler {

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    ReservationService reservationService;

    @Scheduled(fixedRate = 1_000) // 1초
    public void cancelExpiredReservationsForTest() {
        LocalDateTime threshold = LocalDateTime.now().minusSeconds(2); // 2초 뒤에 삭제되게
        List<Reservation> expired = reservationRepository
                .findByStatusAndCreatedAtBefore(ReservationStatus.RESERVED, threshold);

        for (Reservation r : expired) {
            reservationService.cancelReservation(r.getId());
        }
    }
}