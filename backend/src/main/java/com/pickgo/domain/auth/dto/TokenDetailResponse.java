package com.pickgo.domain.auth.dto;

public record TokenDetailResponse(String accessToken) {
    public static TokenDetailResponse of(String accessToken) {
        return new TokenDetailResponse(accessToken);
    }
}
