package com.pickgo.domain.area.area.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AreaGrade {
    PREMIUM("P석"),
    ROYAL("R석"),
    SPECIAL("S석"),
    NORMAL("A석");

    private final String value;
}
