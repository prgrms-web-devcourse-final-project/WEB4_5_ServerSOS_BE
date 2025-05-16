package com.pickgo.domain.auth.token.service;

import static com.pickgo.global.response.RsCode.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pickgo.domain.auth.token.dto.TokenDetailResponse;
import com.pickgo.domain.member.member.entity.Member;
import com.pickgo.domain.member.member.repository.MemberRepository;
import com.pickgo.global.exception.BusinessException;
import com.pickgo.global.jwt.JwtProvider;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    @Value("${custom.jwt.access_token.expiration_minutes}")
    private long accessTokenExpirationMinutes;
    @Value("${custom.jwt.refresh_token.expiration_minutes}")
    private long refreshTokenExpirationMinutes;
    @Value("${custom.jwt.entry_token.expiration_minutes}")
    private long entryTokenExpirationMinutes;
    @Value("${custom.http.secure}")
    private boolean secure;

    @Transactional(readOnly = true)
    public TokenDetailResponse createAccessToken(String refreshToken) {
        if (!jwtProvider.isValidToken(refreshToken)) {
            throw new BusinessException(UNAUTHENTICATED);
        }

        UUID userId = jwtProvider.getUserId(refreshToken);
        Member member = memberRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(NOT_FOUND));

        return TokenDetailResponse.of(genAccessToken(member));
    }

    public void createRefreshToken(Member member, HttpServletResponse response) {
        String newRefreshToken = genRefreshToken(member);
        addRefreshTokenCookie(response, newRefreshToken);
    }

    public String genAccessToken(Member member) {
        return jwtProvider.generateToken(member.getId().toString(), Duration.ofMinutes(accessTokenExpirationMinutes),
                genAuthTokenClaims(member));
    }

    public String genRefreshToken(Member member) {
        return jwtProvider.generateToken(member.getId().toString(), Duration.ofMinutes(refreshTokenExpirationMinutes),
                genAuthTokenClaims(member));
    }

    public String genEntryToken(Long performanceSessionId, UUID userId) {
        return jwtProvider.generateToken(userId.toString(), Duration.ofMinutes(entryTokenExpirationMinutes),
                genEntryTokenClaims(performanceSessionId, userId));
    }

    private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
            .httpOnly(true)
            .secure(secure)
            .path("/")
            .sameSite("None")
            .maxAge(Duration.ofMinutes(refreshTokenExpirationMinutes))
            .build();

        response.setHeader("Set-Cookie", cookie.toString());
    }

    public void removeRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
            .httpOnly(true)
            .secure(secure)
            .path("/")
            .sameSite("None")
            .maxAge(0)
            .build();

        response.setHeader("Set-Cookie", cookie.toString());
    }

    private static Map<String, Object> genEntryTokenClaims(Long performanceSessionId, UUID userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("performance_session_id", performanceSessionId);
        claims.put("user_id", userId);
        return claims;
    }

    private static Map<String, Object> genAuthTokenClaims(Member member) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", member.getId().toString());
        claims.put("authority", member.getAuthority().toString());
        return claims;
    }
}