package com.pickgo.domain.member.member.entity.enums;

public enum Authority {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    private final String role;

    Authority(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return role;
    }
}
