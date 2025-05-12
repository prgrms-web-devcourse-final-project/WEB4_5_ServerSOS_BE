package com.pickgo.domain.member.dto;

public record LoginResponse(String accessToken) {
    public static LoginResponse of(String accessToken) {
        return new LoginResponse(accessToken);
    }
}
