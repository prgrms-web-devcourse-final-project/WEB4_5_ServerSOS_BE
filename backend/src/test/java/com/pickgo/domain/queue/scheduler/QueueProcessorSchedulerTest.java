package com.pickgo.domain.queue.scheduler;

import static org.awaitility.Awaitility.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pickgo.domain.queue.dto.QueueSession;
import com.pickgo.domain.queue.dto.WaitingState;
import com.pickgo.domain.queue.service.QueueService;
import com.pickgo.global.sse.SseHandler;

@ExtendWith(MockitoExtension.class)
class QueueProcessorSchedulerTest {

    @Mock
    private QueueService queueService;
    @Mock
    private SseHandler sseHandler;

    @InjectMocks
    private QueueProcessorScheduler scheduler;

    private final Long performanceSessionId = 1L;
    private final String connectionId = "conn-1";
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setup() {
        when(queueService.getAllPerformanceSessionIds()).thenReturn(List.of(performanceSessionId));
    }

    @Test
    void pollTopCount_결과없으면_enterEntryLine_호출안함() {
        when(queueService.pollTopCount(performanceSessionId, 1)).thenReturn(List.of());
        when(queueService.getLine(performanceSessionId)).thenReturn(List.of());

        scheduler.process();

        verify(queueService, never()).enterEntryLine(any(), any(), any());
    }

    @Test
    void sse_세션없으면_enterEntryLine_호출안함() {
        when(queueService.pollTopCount(performanceSessionId, 1)).thenReturn(List.of(connectionId));
        when(sseHandler.getSession(connectionId, QueueSession.class)).thenReturn(null);
        when(queueService.getLine(performanceSessionId)).thenReturn(List.of());

        scheduler.process();

        verify(queueService, never()).enterEntryLine(any(), any(), any());
    }

    @Test
    void 정상입장시_enterEntryLine_호출됨() {
        when(queueService.pollTopCount(performanceSessionId, 1)).thenReturn(List.of(connectionId));

        QueueSession session = QueueSession.builder()
                .performanceSessionId(performanceSessionId)
                .userId(userId)
                .connectionId(connectionId)
                .build();
        when(sseHandler.getSession(connectionId, QueueSession.class)).thenReturn(session);
        when(queueService.getLine(performanceSessionId)).thenReturn(List.of(connectionId));
        when(queueService.getPosition(performanceSessionId, connectionId)).thenReturn(1);
        when(queueService.getSize(performanceSessionId)).thenReturn(1);

        scheduler.process();

        verify(queueService).enterEntryLine(performanceSessionId, userId, connectionId);

        await().atMost(1, TimeUnit.SECONDS).untilAsserted(() ->
                verify(queueService).publishWaitingState(eq(performanceSessionId), eq(connectionId),
                        any(WaitingState.class))
        );
    }

    @Test
    void waitingStateBroadCast_정상호출된다() {
        when(queueService.pollTopCount(performanceSessionId, 1)).thenReturn(List.of(connectionId));

        QueueSession session = QueueSession.builder()
                .performanceSessionId(performanceSessionId)
                .userId(userId)
                .connectionId(connectionId)
                .build();
        when(sseHandler.getSession(connectionId, QueueSession.class)).thenReturn(session);
        when(queueService.getLine(performanceSessionId)).thenReturn(List.of(connectionId));
        when(queueService.getPosition(performanceSessionId, connectionId)).thenReturn(1);
        when(queueService.getSize(performanceSessionId)).thenReturn(1);

        scheduler.process();

        await().atMost(1, TimeUnit.SECONDS).untilAsserted(() ->
                verify(queueService).publishWaitingState(eq(performanceSessionId), eq(connectionId),
                        any(WaitingState.class))
        );
    }
}
