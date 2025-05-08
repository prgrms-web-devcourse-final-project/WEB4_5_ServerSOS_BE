package com.pickgo.domain.queue.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pickgo.domain.queue.dto.WaitingPosition;
import com.pickgo.domain.queue.repository.WaitingRepository;

@ExtendWith(MockitoExtension.class)
class WaitingServiceTest {

	@Mock
	private WaitingRepository waitingRepository;

	@InjectMocks
	private WaitingService waitingService;

	private final UUID userId = UUID.randomUUID();

	@Test
	@DisplayName("대기열 입장 시 위치 반환")
	void enterWaitingLine_success() {
		when(waitingRepository.add(userId)).thenReturn(3);

		WaitingPosition result = waitingService.enterWaitingLine(userId);

		assertThat(result.position()).isEqualTo(3);
		verify(waitingRepository).add(userId);
	}

	@Test
	void peek_success() {
		waitingService.peek();
		verify(waitingRepository).peek();
	}

	@Test
	void isEmpty_success() {
		when(waitingRepository.isEmpty()).thenReturn(true);

		boolean result = waitingService.isEmpty();

		assertThat(result).isTrue();
		verify(waitingRepository).isEmpty();
	}

	@Test
	void remove_success() {
		waitingService.remove(userId);
		verify(waitingRepository).remove(userId);
	}

	@Test
	void getSize_success() {
		when(waitingRepository.getSize()).thenReturn(5L);

		Long size = waitingService.getSize();
		assertThat(size).isEqualTo(5L);
		verify(waitingRepository).getSize();
	}

	@Test
	void getLine_success() {
		List<UUID> list = List.of(UUID.randomUUID(), UUID.randomUUID());
		when(waitingRepository.getLine()).thenReturn(list);

		List<UUID> result = waitingService.getLine();
		assertThat(result).hasSize(2);
		verify(waitingRepository).getLine();
	}

	@Test
	void clear_success() {
		waitingService.clear();
		verify(waitingRepository).clear();
	}
}

