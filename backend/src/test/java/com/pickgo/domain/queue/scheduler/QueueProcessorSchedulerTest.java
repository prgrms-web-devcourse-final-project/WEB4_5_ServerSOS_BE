package com.pickgo.domain.queue.scheduler;

import com.pickgo.domain.queue.dto.WaitingState;
import com.pickgo.domain.queue.service.QueueService;
import com.pickgo.global.config.thread.ExecutorConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QueueProcessorSchedulerTest {

    @Mock
    private QueueService queueService;

    @Mock
    private ExecutorConfig executorConfig;

    @InjectMocks
    private QueueProcessorScheduler scheduler;

    private final Long performanceSessionId = 1L;
    private final String connectionId = "conn-1";

    private final ThreadPoolTaskExecutor executor = getThreadPoolTaskExecutor();

    private static ThreadPoolTaskExecutor getThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(100);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(0);
        executor.setThreadNamePrefix("async-task-");
        executor.setRejectedExecutionHandler(
                new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Test
    void pollTopCount_결과없으면_enterEntryLine_호출안함() {
        when(executorConfig.queueThreadPoolTaskExecutor()).thenReturn(executor);
        when(queueService.getAllPerformanceSessionIds()).thenReturn(List.of(performanceSessionId));
        when(queueService.pollTopCount(eq(performanceSessionId), anyInt())).thenReturn(List.of());
        when(queueService.getLine(performanceSessionId)).thenReturn(List.of());

        scheduler.process();

        await().atMost(30, TimeUnit.SECONDS).untilAsserted(() ->
                verify(queueService, never()).enterEntryLine(any(), any())
        );
    }

    @Test
    void 정상입장시_enterEntryLine_호출됨() {
        when(executorConfig.queueThreadPoolTaskExecutor()).thenReturn(executor);
        when(queueService.getAllPerformanceSessionIds()).thenReturn(List.of(performanceSessionId));
        when(queueService.pollTopCount(eq(performanceSessionId), anyInt())).thenReturn(List.of(connectionId));
        when(queueService.getLine(performanceSessionId)).thenReturn(List.of(connectionId));
        when(queueService.getPosition(performanceSessionId, connectionId)).thenReturn(1);
        when(queueService.getSize(performanceSessionId)).thenReturn(1);

        scheduler.process();

        await().atMost(30, TimeUnit.SECONDS).untilAsserted(() ->
                verify(queueService).enterEntryLine(performanceSessionId, connectionId)
        );

        await().atMost(30, TimeUnit.SECONDS).untilAsserted(() ->
                verify(queueService).publishWaitingState(eq(performanceSessionId), eq(connectionId),
                        any(WaitingState.class))
        );
    }

    @Test
    void publishWaitingState_정상호출된다() {
        when(executorConfig.queueThreadPoolTaskExecutor()).thenReturn(executor);
        when(queueService.getAllPerformanceSessionIds()).thenReturn(List.of(performanceSessionId));
        when(queueService.pollTopCount(eq(performanceSessionId), anyInt())).thenReturn(List.of(connectionId));
        when(queueService.getLine(performanceSessionId)).thenReturn(List.of(connectionId));
        when(queueService.getPosition(performanceSessionId, connectionId)).thenReturn(1);
        when(queueService.getSize(performanceSessionId)).thenReturn(1);

        scheduler.process();

        await().atMost(30, TimeUnit.SECONDS).untilAsserted(() ->
                verify(queueService).publishWaitingState(eq(performanceSessionId), eq(connectionId),
                        any(WaitingState.class))
        );
    }
}
