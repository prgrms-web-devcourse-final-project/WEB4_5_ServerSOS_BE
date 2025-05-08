package com.pickgo.domain.queue.task;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.pickgo.domain.queue.dto.WaitingPosition;
import com.pickgo.domain.queue.service.WaitingService;
import com.pickgo.domain.queue.sse.SseEmitterHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class WaitingPositionBroadcastTask {

	private final WaitingService waitingService;
	private final SseEmitterHandler sseEmitterHandler;

	/**
	 * 대기열 번호 브로드캐스트
	 */
	@Scheduled(fixedRate = 1000)
	public void broadcast() throws IOException {
		List<UUID> queue = waitingService.getLine();
		for (int i = 0; i < queue.size(); i++) {
			sendWaitingPosition(queue, i);
		}
	}

	private void sendWaitingPosition(List<UUID> queue, int i) throws IOException {
		UUID userId = queue.get(i);
		SseEmitter emitter = sseEmitterHandler.getEmitter(userId);
		if (emitter != null) {
			emitter.send(
				SseEmitter.event()
					.name("position")
					.data(WaitingPosition.of(i + 1))
			);
		}
	}
}
