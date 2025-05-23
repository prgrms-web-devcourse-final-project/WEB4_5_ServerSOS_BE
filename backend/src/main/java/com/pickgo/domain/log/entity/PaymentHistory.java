package com.pickgo.domain.log.entity;

import com.pickgo.domain.log.enums.ActionType;
import com.pickgo.domain.log.enums.ActorType;
import com.pickgo.domain.payment.entity.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentHistory extends BaseLog {

    @Column(nullable = false)
    private Long paymentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private Long reservationId;

    @Column(nullable = false)
    private LocalDateTime paymentTime;

    public PaymentHistory(
            Long paymentId,
            PaymentStatus status,
            Integer amount,
            String orderId,
            Long reservationId,
            LocalDateTime paymentTime,
            String actorId,
            ActorType actorType,
            ActionType action,
            String requestUri,
            String httpMethod,
            String description
    ) {
        super(actorId, actorType, action, requestUri, httpMethod, description);
        this.paymentId = paymentId;
        this.status = status;
        this.amount = amount;
        this.orderId = orderId;
        this.reservationId = reservationId;
        this.paymentTime = paymentTime;
    }
}
