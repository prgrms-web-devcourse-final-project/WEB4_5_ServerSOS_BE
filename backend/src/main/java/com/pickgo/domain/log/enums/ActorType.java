package com.pickgo.domain.log.enums;


import lombok.Getter;

@Getter
public enum ActorType {
    USER("유저"),
    ADMIN("관리자"),
    GUEST("게스트"),
    SYSTEM("시스템");

    private final String description;

    ActorType(String description) {
        this.description = description;
    }
}
