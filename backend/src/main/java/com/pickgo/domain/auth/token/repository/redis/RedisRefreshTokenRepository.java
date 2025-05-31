package com.pickgo.domain.auth.token.repository.redis;

import java.time.Duration;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RedisRefreshTokenRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token";

    public void save(UUID userId, String refreshToken, Duration expiration) {
        redisTemplate.opsForValue().set(getRefreshTokenKey(userId), refreshToken, expiration);
    }

    public String findByUserId(UUID userId) {
        return redisTemplate.opsForValue().get(getRefreshTokenKey(userId));
    }

    public void deleteByUserId(UUID userId) {
        redisTemplate.delete(getRefreshTokenKey(userId));
    }

    private static String getRefreshTokenKey(UUID userId) {
        return REFRESH_TOKEN_PREFIX + ":user_id:" + userId;
    }
}
