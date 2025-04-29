package com.pickgo.domain.payment.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Status {
    PENDING("결제 대기"),
    COMPLETED("결제 완료"),
    FAILED("결제 실패"),
    CANCELLED("결제 취소");

    private final String value;
}
