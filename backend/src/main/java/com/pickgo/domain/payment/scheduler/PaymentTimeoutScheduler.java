package com.pickgo.domain.payment.scheduler;


import com.pickgo.domain.performance.area.seat.entity.SeatStatus;
import com.pickgo.domain.performance.area.seat.event.SeatStatusChangedEvent;
import com.pickgo.domain.log.enums.ActionType;
import com.pickgo.domain.log.enums.ActorType;
import com.pickgo.domain.payment.entity.Payment;
import com.pickgo.domain.payment.entity.PaymentStatus;
import com.pickgo.domain.payment.repository.PaymentRepository;
import com.pickgo.domain.reservation.entity.Reservation;
import com.pickgo.domain.reservation.enums.ReservationStatus;
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
public class PaymentTimeoutScheduler {

    private final PaymentRepository paymentRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final LogWriter logWriter;



    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void deleteStalePayments() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(10);

        List<Payment> stalePayments = paymentRepository.findByStatusAndCreatedAtBefore(
                PaymentStatus.PENDING, threshold
        );

        for (Payment payment : stalePayments) {
            payment.setStatus(PaymentStatus.EXPIRED);

            Reservation reservation = payment.getReservation();
            reservation.setStatus(ReservationStatus.EXPIRED);

            // 좌석 해제 알림 이벤트 발행 (결제가 생성된지 10분이 지난 경우 이벤트 발행)
            reservation.getReservedSeats().forEach(seat -> {
                seat.setStatus(SeatStatus.RELEASED);
                applicationEventPublisher.publishEvent(new SeatStatusChangedEvent(seat));
            });

            reservation.getReservedSeats().clear();

            // 3. 로그 저장
            LogContext systemContext = new LogContext(
                    "Scheduler",
                    "SCHEDULED",
                    "scheduler",
                    ActorType.SYSTEM
            );

            logWriter.writePaymentLog(payment, ActionType.PAYMENT_EXPIRED, systemContext);
        }
    }
}
