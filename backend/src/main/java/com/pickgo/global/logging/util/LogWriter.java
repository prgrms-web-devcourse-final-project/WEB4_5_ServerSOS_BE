package com.pickgo.global.logging.util;

import com.pickgo.domain.log.enums.ActionType;
import com.pickgo.domain.member.member.entity.Member;
import com.pickgo.domain.payment.entity.Payment;
import com.pickgo.domain.reservation.entity.Reservation;
import com.pickgo.global.logging.dto.LogContext;
import com.pickgo.global.logging.service.HistorySaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogWriter {

    private final LogContextUtil logContextUtil;
    private final HistorySaveService historySaveService;

    public void writeReservationLog(Reservation reservation, ActionType action) {
        try {
            LogContext logContext = logContextUtil.extract();
            historySaveService.saveReservationHistory(reservation, logContext, action);
        } catch (Exception e) {
            System.out.println("예약 로그 저장 실패 : " + e.getMessage());
        }
    }

    public void writeReservationLog(Reservation reservation, ActionType action, LogContext logContext) {
        try {
            historySaveService.saveReservationHistory(reservation, logContext, action);
        } catch (Exception e) {
            System.out.println("예약 로그 저장 실패 : " + e.getMessage());
        }
    }

    public void writeMemberLog(Member member, ActionType action) {
        try {
            LogContext logContext = logContextUtil.extract();
            historySaveService.saveMemberHistory(member, logContext, action);
        } catch (Exception e) {
            System.out.println("회원 로그 저장 실패 : " + e.getMessage());
        }
    }

    public void writeMemberLog(Member member, ActionType action, LogContext logContext) {
        try {
            historySaveService.saveMemberHistory(member, logContext, action);
        } catch (Exception e) {
            System.out.println("회원 로그 저장 실패 : " + e.getMessage());
        }
    }

    public void writePaymentLog(Payment payment, ActionType action) {
        try {
            LogContext logContext = logContextUtil.extract();
            historySaveService.savePaymentHistory(payment, logContext, action);
        } catch (Exception e) {
            System.out.println("결제 로그 저장 실패 : " + e.getMessage());
        }
    }

    public void writePaymentLog(Payment payment, ActionType action, LogContext logContext) {
        try {
            historySaveService.savePaymentHistory(payment, logContext, action);
        } catch (Exception e) {
            System.out.println("결제 로그 저장 실패 : " + e.getMessage());
        }
    }

    public void writeExceptionLog(Exception e, ActionType action) {
        LogContext logContext = logContextUtil.extract();
        historySaveService.saveExceptionLog(e, logContext, action);
    }
}
