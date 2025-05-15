package com.pickgo.domain.log.entity;

import com.pickgo.domain.log.enums.ActionType;
import com.pickgo.domain.log.enums.ActorType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private int totalPrice;

    @Column(nullable = false)
    private LocalDateTime reservationTime;

    @Column(nullable = false)
    private Long performanceSessionId;

    @Column(nullable = false)
    private String performanceName;

    @Column(nullable = false)
    private String performanceType;

    @Column(nullable = false)
    private String venueName;

    public ReservationHistory(
            Long reservationId,
            String status,
            int totalPrice,
            LocalDateTime reservationTime,
            Long performanceSessionId,
            String performanceName,
            String performanceType,
            String venueName,
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
        this.performanceName = performanceName;
        this.performanceType = performanceType;
        this.venueName = venueName;
    }
}
