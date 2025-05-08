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

	private static final String ENTRY_SET_KEY = "entry_set"; // 입장한 사용자 집합
	private static final String ENTRY_STATE_PREFIX = "entry_state:"; // 입장 상태 (PENDING, ACTIVE)
	private static final int MAX_ENTRY_SIZE = 1; // 한번에 입장 가능한 사용자 수
	private static final int MAX_PENDING_TIMEOUT_MINUTES = 1; // 입장 후 API 호출 전까지 대기시간
	private final StringRedisTemplate redisTemplate;

	@Override
	public void add(UUID userId) {
		redisTemplate.opsForSet().add(ENTRY_SET_KEY, String.valueOf(userId));
		redisTemplate.opsForValue()
			.set(stateKey(userId), PENDING.name(), Duration.ofMinutes(MAX_PENDING_TIMEOUT_MINUTES));
	}

	@Override
	public boolean isIn(UUID userId) {
		Boolean isMember = redisTemplate.opsForSet().isMember(ENTRY_SET_KEY, String.valueOf(userId));
		return isMember != null && isMember;
	}

	@Override
	public void remove(UUID userId) {
		redisTemplate.opsForSet().remove(ENTRY_SET_KEY, String.valueOf(userId));
		redisTemplate.delete(stateKey(userId));
	}

	@Override
	public Long getSize() {
		return redisTemplate.opsForSet().size(ENTRY_SET_KEY);
	}

	@Override
	public boolean isFull() {
		Long size = redisTemplate.opsForSet().size(ENTRY_SET_KEY);
		return size != null && size >= MAX_ENTRY_SIZE;
	}

	@Override
	public Set<UUID> getAll() {
		Set<String> members = redisTemplate.opsForSet().members(ENTRY_SET_KEY);
		if (members == null || members.isEmpty()) {
			return Set.of();
		}
		return members.stream()
			.map(UUID::fromString)
			.collect(Collectors.toSet());
	}

	@Override
	public void clear() {
		redisTemplate.delete(ENTRY_SET_KEY);
		Set<String> keys = redisTemplate.keys(ENTRY_STATE_PREFIX + "*");
		if (keys != null && !keys.isEmpty()) {
			redisTemplate.delete(keys);
		}
	}

	@Override
	public void setState(UUID userId, EntryState state) {
		redisTemplate.opsForValue().set(stateKey(userId), state.name());
	}

	@Override
	public EntryState getState(UUID userId) {
		return EntryState.from(redisTemplate.opsForValue().get(stateKey(userId)));
	}

	private String stateKey(UUID userId) {
		return ENTRY_STATE_PREFIX + userId;
	}
}
