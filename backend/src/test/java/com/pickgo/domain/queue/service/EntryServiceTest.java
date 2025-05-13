package com.pickgo.domain.queue.service;

import static com.pickgo.domain.queue.enums.EntryState.*;
import static com.pickgo.domain.queue.repository.redis.RedisEntryRepository.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pickgo.domain.queue.repository.EntryRepository;

@ExtendWith(MockitoExtension.class)
class EntryServiceTest {

    private final UUID userId = UUID.randomUUID();
    @Mock
    private EntryRepository entryRepository;
    @InjectMocks
    private EntryService entryService;

    @Test
    void add_success() {
        entryService.add(userId);
        verify(entryRepository).add(userId);
    }

    @Test
    void remove_success() {
        entryService.remove(userId);
        verify(entryRepository).remove(userId);
    }

    @Test
    void getSize_success() {
        when(entryRepository.getSize()).thenReturn(2L);
        assertThat(entryService.getSize()).isEqualTo(2L);
        verify(entryRepository).getSize();
    }

    @Test
    void isFull_success() {
        when(entryRepository.isFull()).thenReturn(true);
        assertThat(entryService.isFull()).isTrue();
        verify(entryRepository).isFull();
    }

    @Test
    void getAll_success() {
        Set<UUID> set = Set.of(UUID.randomUUID());
        when(entryRepository.getAll()).thenReturn(set);
        assertThat(entryService.getAll()).containsExactlyElementsOf(set);
        verify(entryRepository).getAll();
    }

    @Test
    void setState_success() {
        entryService.setState(userId, ACTIVE, MAX_ACTIVE_TIMEOUT_MINUTES);
        verify(entryRepository).setState(userId, ACTIVE, MAX_ACTIVE_TIMEOUT_MINUTES);
    }

    @Test
    void getState_success() {
        when(entryRepository.getState(userId)).thenReturn(ACTIVE);
        assertThat(entryService.getState(userId)).isEqualTo(ACTIVE);
        verify(entryRepository).getState(userId);
    }

    @Test
    void clear_success() {
        entryService.clear();
        verify(entryRepository).clear();
    }
}
