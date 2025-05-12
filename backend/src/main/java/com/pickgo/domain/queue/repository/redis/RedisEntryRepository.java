package com.pickgo.domain.queue.repository.redis;

import static com.pickgo.domain.queue.enums.EntryState.*;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.pickgo.domain.queue.enums.EntryState;
import com.pickgo.domain.queue.repository.EntryRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RedisEntryRepository implements EntryRepository {

	private static final String ENTRY_STATE_PREFIX = "entry_state:"; // 입장 상태 (PENDING, ACTIVE)
	private static final int MAX_ENTRY_SIZE = 1; // 한번에 입장 가능한 사용자 수
	public static final int MAX_PENDING_TIMEOUT_MINUTES = 1; // 입장 후 API 호출 전까지 대기시간
	public static final int MAX_ACTIVE_TIMEOUT_MINUTES = 20; // 입장 후 결제 완료까지 제한시간

	private final StringRedisTemplate redisTemplate;

	@Override
	public void add(UUID userId) {
		// PENDING 상태로 입장
		setState(userId, PENDING, MAX_PENDING_TIMEOUT_MINUTES);
	}

	@Override
	public boolean isIn(UUID userId) {
		return Boolean.TRUE.equals(redisTemplate.hasKey(stateKey(userId)));
	}

	@Override
	public void remove(UUID userId) {
		String stateKey = stateKey(userId);
		redisTemplate.delete(stateKey);
	}

	@Override
	public Long getSize() {
		Set<String> keys = redisTemplate.keys(ENTRY_STATE_PREFIX + "*");
		return keys != null ? (long)keys.size() : 0L;
	}

	@Override
	public boolean isFull() {
		Long size = getSize();
		return size >= MAX_ENTRY_SIZE;
	}

	@Override
	public Set<UUID> getAll() {
		Set<String> keys = redisTemplate.keys(ENTRY_STATE_PREFIX + "*");
		if (keys == null || keys.isEmpty()) {
			return Set.of();
		}
		return keys.stream()
			.map(k -> k.replace(ENTRY_STATE_PREFIX, ""))
			.map(UUID::fromString)
			.collect(Collectors.toSet());
	}

	@Override
	public void clear() {
		Set<String> keys = redisTemplate.keys(ENTRY_STATE_PREFIX + "*");
		if (keys != null && !keys.isEmpty()) {
			redisTemplate.delete(keys);
		}
	}

	@Override
	public void setState(UUID userId, EntryState state, int timeout) {
		redisTemplate.opsForValue().set(stateKey(userId), state.name(), Duration.ofMinutes(timeout));
	}

	@Override
	public EntryState getState(UUID userId) {
		return EntryState.from(redisTemplate.opsForValue().get(stateKey(userId)));
	}

	private String stateKey(UUID userId) {
		return ENTRY_STATE_PREFIX + userId;
	}
}
