package com.pickgo.domain.queue.task;

import java.time.Instant;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.pickgo.domain.queue.sse.SseEmitterHandler;
import com.pickgo.domain.queue.sse.SseEntryMessageRegistry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SseResendTask {

	private static final long TTL_SECONDS = 60; // 1분

	private final SseEntryMessageRegistry sseEntryMessageRegistry;
	private final SseEmitterHandler sseEmitterHandler;

	@Scheduled(fixedRate = 5000)
	public void resend() {
		sseEntryMessageRegistry.getMessageMap().forEach((userId, message) -> {
			// TTL 체크
			if (Instant.now().minusSeconds(TTL_SECONDS).isAfter(message.createdAt())) {
				log.info("Sse message expired - userId: {}", userId);
				sseEntryMessageRegistry.removeMessage(userId);
				return;
			}

			// 전송 시도
			try {
				sseEmitterHandler.getEmitter(userId).send(
					SseEmitter.event()
						.name("ready")
						.data(message)
				);
				sseEntryMessageRegistry.removeMessage(userId);
			} catch (Exception e) {
				log.error("Sse resend error - userId: {}", userId, e);
			}
		});
	}
}
