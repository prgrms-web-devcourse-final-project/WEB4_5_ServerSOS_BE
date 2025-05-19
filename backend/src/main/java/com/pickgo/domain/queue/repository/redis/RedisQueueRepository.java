package com.pickgo.domain.queue.repository.redis;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Repository;

import com.pickgo.domain.queue.repository.QueueRepository;

import lombok.RequiredArgsConstructor;

/**
 * 대기열 관련 Repository (Redis)
 */
@Repository
@RequiredArgsConstructor
public class RedisQueueRepository implements QueueRepository {

    private static final String WAITING_LINE_PREFIX = "waiting_line"; // 대기열
    private final StringRedisTemplate redisTemplate;

    /**
     * 대기열에 커넥션을 추가
     */
    @Override
    public int add(Long performanceSessionId, String connectionId, String serverId) {
        long score = System.currentTimeMillis();

        // 대기열에 커넥션 추가 (score는 현재 시간 기준)
        String waitingKey = getWaitingKey(performanceSessionId);
        redisTemplate.opsForZSet().add(waitingKey, connectionId, score);

        // 추가된 커넥션의 대기열 순번 반환
        return getPosition(performanceSessionId, connectionId);
    }

    /**
     * 특정 커넥션의 대기열 순번 조회 (1부터 시작, 없으면 -1)
     */
    @Override
    public int getPosition(Long performanceSessionId, String connectionId) {
        String waitingKey = getWaitingKey(performanceSessionId);
        Long rank = redisTemplate.opsForZSet().rank(waitingKey, connectionId);
        return rank != null ? rank.intValue() + 1 : -1;
    }

    /**
     * 대기열에서 앞에서부터 count 만큼 커넥션 꺼내고 대기열에서 제거
     */
    @Override
    public List<String> pollTopCount(Long performanceSessionId, int count) {
        if (count == 0) {
            return Collections.emptyList();
        }

        String waitingKey = getWaitingKey(performanceSessionId);
        Set<TypedTuple<String>> popped = redisTemplate.opsForZSet().popMin(waitingKey, count);

        if (popped == null || popped.isEmpty()) {
            return Collections.emptyList();
        }

        return popped.stream()
                .map(TypedTuple::getValue)
                .toList();
    }

    /**
     * 대기열에서 특정 커넥션 제거
     */
    @Override
    public void remove(Long performanceSessionId, String connectionId) {
        // 대기열에서 해당 커넥션 제거
        String waitingKey = getWaitingKey(performanceSessionId);
        redisTemplate.opsForZSet().remove(waitingKey, connectionId);
    }

    /**
     * 대기열이 비어있는지 여부 반환
     */
    @Override
    public boolean isEmpty(Long performanceSessionId) {
        String waitingKey = getWaitingKey(performanceSessionId);
        Long size = redisTemplate.opsForZSet().size(waitingKey);
        return size == null || size == 0;
    }

    /**
     * 현재 대기열에 존재하는 커넥션 수 반환
     */
    @Override
    public int getSize(Long performanceSessionId) {
        String waitingKey = getWaitingKey(performanceSessionId);
        Long size = redisTemplate.opsForZSet().size(waitingKey);
        return size == null ? 0 : size.intValue();
    }

    /**
     * 전체 대기열 키 목록 조회
     */
    @Override
    public List<String> getAllKeys() {
        Set<String> keys = redisTemplate.keys(WAITING_LINE_PREFIX + ":*");
        if (keys == null) {
            return List.of();
        }
        return keys.stream().toList();
    }

    /**
     * 특정 대기열 전체 조회
     */
    @Override
    public List<String> getLine(Long performanceSessionId) {
        String waitingKey = getWaitingKey(performanceSessionId);
        Set<String> set = redisTemplate.opsForZSet().range(waitingKey, 0, -1);
        if (set == null)
            return List.of();
        return set.stream().toList();
    }

    /**
     * 대기열 모두 제거
     */
    @Override
    public void clearAll() {
        Set<String> keys = redisTemplate.keys(WAITING_LINE_PREFIX + ":*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 특정 커넥션이 대기열에 존재하는지 여부 반환
     */
    @Override
    public boolean isIn(Long performanceSessionId, String connectionId) {
        String waitingKey = getWaitingKey(performanceSessionId);
        Double score = redisTemplate.opsForZSet().score(waitingKey, connectionId);
        return score != null;
    }

    /**
     * 대기열 키 반환
     */
    private static String getWaitingKey(Long performanceSessionId) {
        return WAITING_LINE_PREFIX + ":performance_session_id:" + performanceSessionId;
    }
}
