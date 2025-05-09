package com.pickgo.domain.log.entity;

import com.pickgo.domain.log.enums.ActionType;
import com.pickgo.domain.log.enums.ActorType;
import com.pickgo.domain.reservation.enums.ReservationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationHistory extends BaseLog {

    @Column(nullable = false)
    private Long reservationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(nullable = false)
    private int totalPrice;

    @Column(nullable = false)
    private LocalDateTime reservationTime;

    @Column(nullable = false)
    private Long performanceSessionId;

    public ReservationHistory(
            Long reservationId,
            ReservationStatus status,
            int totalPrice,
            LocalDateTime reservationTime,
            Long performanceSessionId,
            String actorId,
            ActorType actorType,
            ActionType action,
            String requestUri,
            String httpMethod,
            String description
    ) {
        super(actorId, actorType, action, requestUri, httpMethod, description);
        this.reservationId = reservationId;
        this.status = status;
        this.totalPrice = totalPrice;
        this.reservationTime = reservationTime;
        this.performanceSessionId = performanceSessionId;
    }
}
