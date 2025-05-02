package com.pickgo.domain.reservation.enums;


import lombok.Getter;

@Getter
public enum ReservationStatus {
    RESERVED("예약 완료"),
    CANCELED("예약 취소"),
    PAID("결제 완료");

    private final String description;

    ReservationStatus(String description) {
        this.description = description;
    }
}