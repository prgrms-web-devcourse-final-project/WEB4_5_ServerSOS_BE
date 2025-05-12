package com.pickgo.domain.oauth.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoToken(
    @JsonProperty("token_type")
    String tokenType,

    @JsonProperty("access_token")
    String accessToken,

    @JsonProperty("refresh_token")
    String refreshToken,

    @JsonProperty("expires_in")
    Long expiresIn,

    @JsonProperty("scope")
    String agreeScope, // 동의한 사용자 정보 제공 목록 (ex: nickname, profile, email 등)

    @JsonProperty("refresh_token_expires_in")
    Long refreshTokenExpiresIn
) {
}
