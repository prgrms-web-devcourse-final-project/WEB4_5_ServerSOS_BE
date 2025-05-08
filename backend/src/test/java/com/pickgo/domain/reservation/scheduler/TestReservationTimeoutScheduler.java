package com.pickgo.domain.reservation.scheduler;

import com.pickgo.domain.payment.repository.PaymentRepository;
import com.pickgo.domain.reservation.entity.Reservation;
import com.pickgo.domain.reservation.enums.ReservationStatus;
import com.pickgo.domain.reservation.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Profile("test")
public class TestReservationTimeoutScheduler {

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Scheduled(fixedRate = 1_000) // 1초
    @Transactional
    public void cancelExpiredReservationsForTest() {
        LocalDateTime threshold = LocalDateTime.now().minusSeconds(2); // 2초 뒤에 삭제되게

        List<Reservation> expired = reservationRepository
                .findByStatusAndCreatedAtBefore(ReservationStatus.RESERVED, threshold);

        // 2. 만료된 예약이 있을때 결제가 생성되어있지 않으면(예약만 하고 대기)
        // -> 예약은 만료 처리되고 좌석은 release됨
        for (Reservation reservation : expired) {
            boolean hasPayment = paymentRepository.existsByReservation(reservation);
            if (!hasPayment) {
                reservation.setStatus(ReservationStatus.EXPIRED);
                reservation.releaseSeats();
            }
        }
    }
}
