package com.pickgo.domain.queue.sse;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
public class SseEmitterRegistry {

    private final ConcurrentMap<UUID, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    public void addEmitter(UUID userId, SseEmitter emitter) {
        emitterMap.put(userId, emitter);
    }

    public SseEmitter getEmitter(UUID userId) {
        return emitterMap.get(userId);
    }

    public void removeEmitter(UUID userId) {
        emitterMap.remove(userId);
    }
}
