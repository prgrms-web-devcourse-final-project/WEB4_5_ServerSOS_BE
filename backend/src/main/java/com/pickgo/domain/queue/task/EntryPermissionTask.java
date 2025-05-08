package com.pickgo.domain.queue.task;

import static com.pickgo.domain.member.entity.enums.Authority.*;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.pickgo.domain.auth.service.TokenService;
import com.pickgo.domain.member.entity.Member;
import com.pickgo.domain.queue.dto.EntryPermission;
import com.pickgo.domain.queue.service.EntryService;
import com.pickgo.domain.queue.service.WaitingService;
import com.pickgo.domain.queue.sse.SseEmitterHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EntryPermissionTask {

	private static final long LOCK_WAIT_TIME_SECONDS = 1; // 락 획득 대기시간
	private final RedissonClient redissonClient;
	private final SseEmitterHandler sseEmitterHandler;
	private final WaitingService waitingService;
	private final EntryService entryService;
	private final TokenService tokenService;
	private UUID userId;
	private SseEmitter emitter;

	/**
	 * 입장 처리
	 */
	@Scheduled(fixedRate = 1000)
	public void enter() throws IOException {
		RLock lock = redissonClient.getLock("entry-lock");
		boolean acquired = false;

		try {
			// 락 획득 대기, 작업 완료 시까지 락 해제 안함
			acquired = lock.tryLock(LOCK_WAIT_TIME_SECONDS, TimeUnit.SECONDS);
			if (!acquired) {
				log.warn("락 획득 실패 - userId: {}", userId);
				return;
			}

			// 입장 가능한지 확인
			if (entryService.isFull())
				return;

			// 대기열 첫번째 유저
			userId = waitingService.peek();
			if (userId == null)
				return;

			// 현재 서버에 emitter가 있는 경우에만 처리
			emitter = sseEmitterHandler.getEmitter(userId);
			if (emitter == null)
				return;

			// 대기열에서 제거 후 입장
			waitingService.remove(userId);
			entryService.add(userId);

		} catch (InterruptedException e) {
			log.error("락 대기 중 인터럽트 발생", e);
			Thread.currentThread().interrupt();
			return;

		} finally {
			if (acquired)
				lock.unlock();
		}

		// 클라이언트에게 입장 알림
		sendEntryPermission();
	}

	private void sendEntryPermission() throws IOException {
		Member member = Member.builder()
			.id(userId)
			.authority(USER)
			.build();

		// 입장 토큰 발행 및 메시지 전송
		String entryToken = tokenService.genEntryToken(member);
		emitter.send(
			SseEmitter.event()
				.name("ready")
				.data(EntryPermission.of(entryToken))
		);

		emitter.complete();
	}
}

