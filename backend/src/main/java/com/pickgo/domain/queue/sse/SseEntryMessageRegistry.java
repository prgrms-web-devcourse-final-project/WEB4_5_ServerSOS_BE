package com.pickgo.domain.queue.sse;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Component;

import com.pickgo.domain.queue.dto.EntryPermission;

import lombok.Getter;

/**
 * 전송 실패로 인해 보내지 못한 입장 메시지를 저장하는 저장소
 */
@Getter
@Component
public class SseEntryMessageRegistry {

	private final ConcurrentMap<UUID, EntryPermission> messageMap = new ConcurrentHashMap<>();

	public void addMessage(UUID userId, EntryPermission message) {
		messageMap.put(userId, message);
	}

	public EntryPermission getMessage(UUID userId) {
		return messageMap.get(userId);
	}

	public void removeMessage(UUID userId) {
		messageMap.remove(userId);
	}
}
