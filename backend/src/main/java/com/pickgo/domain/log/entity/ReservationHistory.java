package com.pickgo.domain.log.entity;

import com.pickgo.domain.reservation.enums.ReservationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
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

}
