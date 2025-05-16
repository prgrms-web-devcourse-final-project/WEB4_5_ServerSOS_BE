package com.pickgo.global.infra.stream.redis;

import java.util.Map;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * Redis Stream 메시지를 발행하는 Publisher
 */
@Component
@RequiredArgsConstructor
public class RedisStreamPublisher {

    private final StringRedisTemplate redisTemplate;

    /**
     * 메시지 발행
     */
    public void publish(String key, Map<String, String> data) {
        redisTemplate.opsForStream().add(key, data);
    }
}
