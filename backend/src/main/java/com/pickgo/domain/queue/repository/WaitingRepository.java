package com.pickgo.domain.queue.repository;

import java.util.List;
import java.util.UUID;

/**
 * 대기열
 */
public interface WaitingRepository {
	int add(UUID userId);

	UUID peek();

	boolean isEmpty();

	Long getSize();

	void remove(UUID userId);

	int getPosition(UUID userId);

	List<UUID> getLine();

	void clear();
}
