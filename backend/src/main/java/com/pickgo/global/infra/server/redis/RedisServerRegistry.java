package com.pickgo.global.infra.server.redis;

import java.util.NoSuchElementException;
import java.util.Set;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.pickgo.global.infra.server.ServerRegistry;

import lombok.RequiredArgsConstructor;

/**
 * 서버 식별자 저장소 (Redis)
 */
@Repository
@RequiredArgsConstructor
public class RedisServerRegistry implements ServerRegistry {

    private static final String SERVER_PREFIX = "connection_server"; // 커넥션 서버
    private final StringRedisTemplate redisTemplate;

    @Override
    public void save(String connectionId, String serverId) {
        redisTemplate.opsForValue().set(getServerKey(connectionId), serverId);
    }

    @Override
    public String getServerId(String connectionId) {
        String serverId = redisTemplate.opsForValue().get(getServerKey(connectionId));
        if (serverId == null) {
            throw new NoSuchElementException("No connected server found for connection: " + connectionId);
        }
        return serverId;
    }

    @Override
    public void remove(String connectionId) {
        redisTemplate.delete(getServerKey(connectionId));
    }

    private String getServerKey(String connectionId) {
        return SERVER_PREFIX + ":connection_id:" + connectionId;
    }

    @Override
    public void clearAll() {
        Set<String> keys = redisTemplate.keys(SERVER_PREFIX + ":*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
