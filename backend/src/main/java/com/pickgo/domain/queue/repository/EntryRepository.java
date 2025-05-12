package com.pickgo.domain.queue.repository;

import java.util.Set;
import java.util.UUID;

import com.pickgo.domain.queue.enums.EntryState;

/**
 * 입장한 사용자 집합
 */
public interface EntryRepository {
	void add(UUID userId);

	boolean isIn(UUID userId);

	void remove(UUID userId);

	Long getSize();

	boolean isFull();

	Set<UUID> getAll();

	void setState(UUID userId, EntryState state, int timeout);

	EntryState getState(UUID userId);

	void clear();
}
