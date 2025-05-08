package com.pickgo.domain.queue.service;

import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.pickgo.domain.queue.enums.EntryState;
import com.pickgo.domain.queue.repository.EntryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EntryService {

	private final EntryRepository entryRepository;

	public void add(UUID userId) {
		entryRepository.add(userId);
	}

	public void remove(UUID userId) {
		entryRepository.remove(userId);
	}

	public Long getSize() {
		return entryRepository.getSize();
	}

	public boolean isFull() {
		return entryRepository.isFull();
	}

	public Set<UUID> getAll() {
		return entryRepository.getAll();
	}

	public void setState(UUID userId, EntryState state) {
		entryRepository.setState(userId, state);
	}

	public EntryState getState(UUID userId) {
		return entryRepository.getState(userId);
	}

	public void clear() {
		entryRepository.clear();
	}
}
