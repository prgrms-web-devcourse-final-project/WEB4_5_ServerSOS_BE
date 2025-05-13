package com.pickgo.domain.queue.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.pickgo.domain.queue.dto.WaitingPosition;
import com.pickgo.domain.queue.repository.WaitingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WaitingService {

    private final WaitingRepository waitingRepository;

    public WaitingPosition enterWaitingLine(UUID userId) {
        int position = waitingRepository.add(userId);
        return WaitingPosition.of(position);
    }

    public UUID peek() {
        return waitingRepository.peek();
    }

    public boolean isEmpty() {
        return waitingRepository.isEmpty();
    }

    public void remove(UUID userId) {
        waitingRepository.remove(userId);
    }

    public Long getSize() {
        return waitingRepository.getSize();
    }

    public List<UUID> getLine() {
        return waitingRepository.getLine();
    }

    public void clear() {
        waitingRepository.clear();
    }

    public boolean isIn(UUID userId) {
        return waitingRepository.isIn(userId);
    }
}
