package com.pickgo.global.infra.metric.repository.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisMetricRepository {

    private final StringRedisTemplate redisTemplate;
    private static final String TPS_KEY = "metric:tps:latest";

    public void saveTps(long tps) {
        redisTemplate.opsForValue().set(TPS_KEY, String.valueOf(tps));
    }

    public Long getLatestTps() {
        String val = redisTemplate.opsForValue().get(TPS_KEY);
        if (val == null)
            return null;
        return Long.parseLong(val);
    }
}
