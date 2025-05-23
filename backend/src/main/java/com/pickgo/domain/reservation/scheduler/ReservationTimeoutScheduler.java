package com.pickgo.domain.reservation.scheduler;


import com.pickgo.domain.performance.area.seat.entity.ReservedSeat;
import com.pickgo.domain.performance.area.seat.entity.SeatStatus;
import com.pickgo.domain.performance.area.seat.event.SeatStatusChangedEvent;
import com.pickgo.domain.log.enums.ActionType;
import com.pickgo.domain.log.enums.ActorType;
import com.pickgo.domain.payment.repository.PaymentRepository;
import com.pickgo.domain.reservation.entity.Reservation;
import com.pickgo.domain.reservation.enums.ReservationStatus;
import com.pickgo.domain.reservation.repository.ReservationRepository;
import com.pickgo.global.logging.dto.LogContext;
import com.pickgo.global.logging.util.LogWriter;
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


    private final LogWriter logWriter;

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

                for (ReservedSeat seat : reservation.getReservedSeats()) { //예약에 연결된 모든 좌석 반복
                    seat.setStatus(SeatStatus.RELEASED);
                    applicationEventPublisher.publishEvent(new SeatStatusChangedEvent(seat)); // 좌석 상태 변경 이벤트 발행(5분이 지난 예약 중 결제 되지 않은 예약)
                }
                //DB 삭제 후 객체 내부 상태 맞춰주기
                reservation.getReservedSeats().clear();

                // 3. 로그 저장
                LogContext systemContext = new LogContext(
                        "Scheduler",
                        "SCHEDULED",
                        "scheduler",
                        ActorType.SYSTEM
                );

                logWriter.writeReservationLog(reservation, ActionType.RESERVATION_EXPIRED, systemContext);
            }

        }


    }
}
