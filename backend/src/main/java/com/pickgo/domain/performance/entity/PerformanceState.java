package com.pickgo.domain.performance.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PerformanceState {
    SCHEDULED("공연예정"),
    ONGROIN("공연중"),
    COMPLETED("공연완료");

    private final String value;
}
