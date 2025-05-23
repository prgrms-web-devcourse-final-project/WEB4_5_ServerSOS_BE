package com.pickgo.domain.performance.performance.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PerformanceState {
    SCHEDULED("공연예정"),
    ONGOING("공연중"),
    COMPLETED("공연완료");

    private final String value;
}
