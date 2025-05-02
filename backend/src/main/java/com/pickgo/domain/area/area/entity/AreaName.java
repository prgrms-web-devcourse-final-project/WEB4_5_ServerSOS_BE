package com.pickgo.domain.area.area.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AreaName {
    VIP("VIP 구역"),
    A("A 구역"),
    B("B 구역"),
    C("C 구역"),
    D("D 구역");

    private final String value;
}
