package com.pickgo.domain.queue.task;

import java.util.Set;
import java.util.UUID;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pickgo.domain.queue.enums.EntryState;
import com.pickgo.domain.queue.service.EntryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EntryMonitoringTask {

	private final EntryService entryService;

	/**
	 * 입장한 사용자가 API를 호출했는지 모니터링
	 */
	@Scheduled(fixedRate = 60000)
	public void monitor() {
		Set<UUID> userIds = entryService.getAll();
		for (UUID userId : userIds) {
			EntryState state = entryService.getState(userId);

			// API를 ttl 동안 호출하지 않으면 입장 취소
			if (state == null) {
				entryService.remove(userId);
			}
		}
	}
}
