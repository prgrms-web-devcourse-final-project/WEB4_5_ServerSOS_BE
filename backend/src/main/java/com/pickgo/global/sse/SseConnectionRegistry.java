package com.pickgo.global.sse;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Component;

/**
 * SSE Connection 저장소
 */
@Component
public class SseConnectionRegistry {

    private final ConcurrentMap<String, SseConnection> connectionMap = new ConcurrentHashMap<>();

    public void add(String connectionId, SseConnection info) {
        connectionMap.put(connectionId, info);
    }

    public SseConnection getConnection(String connectionId) {
        return connectionMap.get(connectionId);
    }

    public void remove(String connectionId) {
        connectionMap.remove(connectionId);
    }
}
