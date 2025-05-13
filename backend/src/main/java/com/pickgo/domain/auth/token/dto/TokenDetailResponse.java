package com.pickgo.domain.auth.token.dto;

public record TokenDetailResponse(String accessToken) {
    public static TokenDetailResponse of(String accessToken) {
        return new TokenDetailResponse(accessToken);
    }
}
