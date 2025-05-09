package com.pickgo.domain.reservation.scheduler;

import com.pickgo.domain.area.seat.entity.ReservedSeat;
import com.pickgo.domain.area.seat.entity.SeatStatus;
import com.pickgo.domain.area.seat.event.SeatStatusChangedEvent;
import com.pickgo.domain.area.seat.repository.ReservedSeatRepository;
import com.pickgo.domain.payment.repository.PaymentRepository;
import com.pickgo.domain.reservation.entity.Reservation;
import com.pickgo.domain.reservation.enums.ReservationStatus;
import com.pickgo.domain.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationTimeoutScheduler {
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ReservedSeatRepository reservedSeatRepository;

    @Scheduled(fixedRate = 60_000) // 매 1분마다 실행
    @Transactional
    public void cancelExpiredReservations() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(5);

        // 1. 5분 넘었고, RESERVED 상태인 것 조회
        List<Reservation> expired = reservationRepository
                .findByStatusAndCreatedAtBefore(ReservationStatus.RESERVED, threshold);

        // 2. 만료된 예약이 있을때 결제가 생성되어있지 않으면(예약만 하고 대기)
        // -> 예약은 만료 처리되고 좌석은 release됨
        for (Reservation reservation : expired) {
            boolean hasPayment = paymentRepository.existsByReservation(reservation);
            if (!hasPayment) {
                reservation.setStatus(ReservationStatus.EXPIRED);
                reservation.getReservedSeats().clear();

                for (ReservedSeat seat : reservation.getReservedSeats()) {
                    seat.setStatus(SeatStatus.PENDING);
                    reservedSeatRepository.save(seat);
                    applicationEventPublisher.publishEvent(new SeatStatusChangedEvent(seat));
                }


            }
            // 이후 좌석 연결 제거
            reservation.getReservedSeats().clear();

        }
    }
}
