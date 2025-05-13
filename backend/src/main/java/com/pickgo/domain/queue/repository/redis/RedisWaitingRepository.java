package com.pickgo.domain.queue.repository.redis;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.pickgo.domain.queue.repository.WaitingRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RedisWaitingRepository implements WaitingRepository {

    private static final String WAITING_LINE_KEY = "waiting_line"; // 대기열
    private final StringRedisTemplate redisTemplate;

    @Override
    public int add(UUID userId) {
        long score = System.currentTimeMillis();
        redisTemplate.opsForZSet().add(WAITING_LINE_KEY, String.valueOf(userId), score);
        return getPosition(userId);
    }

    @Override
    public int getPosition(UUID userId) {
        Long rank = redisTemplate.opsForZSet().rank(WAITING_LINE_KEY, String.valueOf(userId));
        return rank != null ? rank.intValue() + 1 : -1;
    }

    @Override
    public UUID peek() {
        Set<String> first = redisTemplate.opsForZSet().range(WAITING_LINE_KEY, 0, 0);
        if (first == null || first.isEmpty())
            return null;
        String userId = first.iterator().next();
        return UUID.fromString(userId);
    }

    @Override
    public boolean isEmpty() {
        Long size = redisTemplate.opsForZSet().size(WAITING_LINE_KEY);
        return size == null || size == 0;
    }

    @Override
    public void remove(UUID userId) {
        redisTemplate.opsForZSet().remove(WAITING_LINE_KEY, String.valueOf(userId));
    }

    @Override
    public Long getSize() {
        return redisTemplate.opsForZSet().size(WAITING_LINE_KEY);
    }

    @Override
    public List<UUID> getLine() {
        Set<String> set = redisTemplate.opsForZSet().range(WAITING_LINE_KEY, 0, -1);
        if (set == null)
            return List.of();
        return set.stream().map(UUID::fromString).toList();
    }

    @Override
    public void clear() {
        redisTemplate.delete(WAITING_LINE_KEY);
    }

    @Override
    public boolean isIn(UUID userId) {
        Double score = redisTemplate.opsForZSet().score(WAITING_LINE_KEY, String.valueOf(userId));
        return score != null;
    }
}
