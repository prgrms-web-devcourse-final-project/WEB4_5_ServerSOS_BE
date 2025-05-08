package com.pickgo.domain.queue.sse;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.pickgo.domain.queue.repository.WaitingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SseEmitterHandler {

	private final static long TIMEOUT_MILLIS = -1L; // Emitter 연결 지속 시간 (무제한)
	private final SseEmitterRegistry sseEmitterRegistry;
	private final WaitingRepository waitingRepository;

	public SseEmitter subscribe(UUID userId) {
		SseEmitter emitter = new SseEmitter(TIMEOUT_MILLIS);
		sseEmitterRegistry.addEmitter(userId, emitter);

		Runnable cleanup = () -> {
			log.info("대기열에서 제거됨 - userId: {}", userId);
			removeEmitter(userId);
			waitingRepository.remove(userId);
		};

		emitter.onCompletion(cleanup);
		emitter.onTimeout(cleanup);
		emitter.onError(e -> {
			log.warn("[SSE] 연결 끊김 또는 예외 - userId: {}", userId, e);
			cleanup.run();
		});

		return emitter;
	}

	public SseEmitter getEmitter(UUID userId) {
		return sseEmitterRegistry.getEmitter(userId);
	}

	public void removeEmitter(UUID userId) {
		sseEmitterRegistry.removeEmitter(userId);
	}
}
