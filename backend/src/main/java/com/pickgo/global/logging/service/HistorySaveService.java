package com.pickgo.global.logging.service;


import com.pickgo.domain.log.entity.*;
import com.pickgo.domain.log.enums.ActionType;
import com.pickgo.domain.log.repository.*;
import com.pickgo.domain.member.member.entity.Member;
import com.pickgo.domain.payment.entity.Payment;
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
    private final MemberHistoryRepository memberHistoryRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final ExceptionLogRepository exceptionLogRepository;
    private final AccessHistoryRepository accessHistoryRepository;

    /***
     * 로깅에서 오류가 나도 메소드가 정상 작동할 수 있도록 별도 트랜잭션 생성
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveReservationHistory(Reservation reservation, LogContext ctx, ActionType actionType) {
        ReservationHistory history = new ReservationHistory(
                reservation.getId(),
                reservation.getStatus().name(),
                reservation.getTotalPrice(),
                reservation.getCreatedAt(),
                reservation.getPerformanceSession().getId(),
                reservation.getPerformanceSession().getPerformance().getName(),
                reservation.getPerformanceSession().getPerformance().getType().name(),
                reservation.getPerformanceSession().getPerformance().getVenue().getName(),
                ctx.actorId(),
                ctx.actorType(),
                actionType,
                ctx.requestUri(),
                ctx.httpMethod(),
                "예약 : " + actionType.getDescription()
        );
        reservationHistoryRepository.save(history);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveMemberHistory(Member member, LogContext ctx, ActionType action) {
        MemberHistory history = new MemberHistory(
                member.getEmail(),
                member.getNickname(),
                member.getAuthority(),
                member.getSocialProvider(),
                ctx.actorId(),
                ctx.actorType(),
                action,
                ctx.requestUri(),
                ctx.httpMethod(),
                "회원 : " + action.getDescription()
        );
        memberHistoryRepository.save(history);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void savePaymentHistory(Payment payment, LogContext ctx, ActionType action) {
        PaymentHistory history = new PaymentHistory(
                payment.getId(),
                payment.getStatus(),
                payment.getAmount(),
                payment.getOrderId(),
                payment.getReservation().getId(),
                payment.getCreatedAt(),
                ctx.actorId(),
                ctx.actorType(),
                action,
                ctx.requestUri(),
                ctx.httpMethod(),
                "결제 : " + action.getDescription()
        );
        paymentHistoryRepository.save(history);
    }


    @Transactional
    public void saveExceptionLog(Exception e, LogContext ctx, ActionType actionType) {
        ExceptionHistory exceptionHistory = new ExceptionHistory(
                e.getClass().getSimpleName(),
                ctx.actorId(),
                ctx.actorType(),
                actionType,
                ctx.requestUri(),
                ctx.httpMethod(),
                "예외 메시지 : " + e.getMessage()
        );

        exceptionLogRepository.save(exceptionHistory);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAccessHistory(LogContext ctx) {
        AccessHistory accessHistory = new AccessHistory(
                ctx.actorId(),
                ctx.actorType(),
                ctx.requestUri(),
                ctx.httpMethod()
        );

        accessHistoryRepository.save(accessHistory);
    }
}
