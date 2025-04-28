package com.pickgo.performance.seat.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SeatStatus {
    AVAILABLE("예약 가능"),
    PENDING("예약 중"),
    RESERVED("예약 완료");

    private final String value;
}
