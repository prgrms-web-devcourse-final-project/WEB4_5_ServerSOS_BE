package com.pickgo.domain.queue.stream;

import java.util.Map;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.pickgo.domain.queue.repository.QueueRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueueStreamPublisher {

    private final StringRedisTemplate redisTemplate;
    private final QueueRepository queueRepository;

    /**
     * 대기열 관련 publish
     */
    public void publish(Long performanceSessionId, String connectionId, Map<String, String> data) {
        String serverId = queueRepository.getServerId(performanceSessionId, connectionId);
        redisTemplate.opsForStream().add("queue_stream:serverId:" + serverId, data);
    }
}
