package com.pickgo.domain.performance.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PerformanceType {
    PLAY("연극"),
    DANCE("무용"),
    KOREAN("국악"),
    CONCERT("콘서트"),
    CLASSIC("클래식"),
    MUSICAL("뮤지컬");

    private final String value;
}
