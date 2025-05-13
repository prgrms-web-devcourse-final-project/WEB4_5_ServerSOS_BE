package com.pickgo.domain.performance.area.seat.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SeatStatus {
    PENDING("예약중"),
    RESERVED("예약완료"),
    RELEASED("선택 가능");

    private final String value;
}
