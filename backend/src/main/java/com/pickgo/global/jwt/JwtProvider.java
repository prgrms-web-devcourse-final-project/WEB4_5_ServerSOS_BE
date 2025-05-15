package com.pickgo.global.jwt;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.pickgo.domain.member.member.dto.MemberPrincipal;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final static String TOKEN_PREFIX = "Bearer "; // 토큰은 Bearer로 시작해야함
    private final JwtProperties jwtProperties;

    /**
     * 토큰 생성
     **/
    public String generateToken(String subject, Duration expiredAt, Map<String, Object> claims) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiredAt.toMillis());

        // 토큰 헤더 설정
        Header jwtHeader = Jwts.header().type("JWT").build();

        // 서명에 사용할 Secret Key 획득
        SecretKey key = getSecretKey();

        JwtBuilder builder = Jwts.builder()
                .header().add(jwtHeader).and()              // 토큰 타입: JWT
                .signWith(key, Jwts.SIG.HS256)              // 암호화 방식: HMAC-SHA256, secret key 사용
                .subject(subject)                           // 토큰의 subject (예: 사용자 ID)
                .issuer(jwtProperties.getIssuer())          // 토큰 발급자
                .issuedAt(now)                              // 토큰 발급일
                .expiration(expiryDate);                    // 토큰 만료일

        // 추가로 담을 claims (id, authority 등)
        if (claims != null && !claims.isEmpty()) {
            builder.claims(claims);
        }

        // 토큰 서명 및 최종 문자열 생성
        return builder.compact();
    }

    /**
     * 유효한 토큰인지 검증 (토큰이 유효하면 사용자 인증 완료)
     **/
    public boolean isValidToken(String token) { // 검증하려면 token에서 "Bearer " 없어야 됨
        try {
            // 토큰의 서명이 올바른지, 만료되지 않았는지 확인
            Jwts.parser()
                .verifyWith(getSecretKey()) // secret_key를 사용해서 토큰 복호화
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 토큰으로부터 인증 정보 획득
     **/
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);

        // enum Authority 하나를 가져온다
        String authority = claims.get("authority", String.class);
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(authority);

        // Authentication 생성
        return new UsernamePasswordAuthenticationToken(
            new MemberPrincipal(getUserId(token)), // principal (id)
            null, // JWT는 credentials가 필요 없음
            List.of(grantedAuthority) // 권한 리스트
        );
    }

    /**
     * HTTP Header에 정의된 Bearer token 획득
     **/
    public String getTokenFromHeader(String header) {
        if (header == null || !header.startsWith(TOKEN_PREFIX))
            return null;
        return header.substring(TOKEN_PREFIX.length());
    }

    /**
     * Claims로부터 사용자 Id 획득
     **/
    public UUID getUserId(String token) {
        Claims claims = getClaims(token);
        return UUID.fromString(claims.get("id", String.class));
    }

    /**
     * 토큰으로부터 Claims(토큰 내용) 획득
     **/
    private Claims getClaims(String token) {
        return Jwts.parser() // claim 조회
            .verifyWith(getSecretKey()) // secret_key를 사용해서 토큰 복호화
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
    }
}
