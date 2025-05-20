package com.pickgo.domain.queue.scheduler;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;

import org.springframework.core.env.Environment;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.pickgo.domain.queue.dto.WaitingState;
import com.pickgo.domain.queue.service.QueueService;
import com.pickgo.global.config.thread.ExecutorConfig;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

/**
 * 대기열 입장 처리 및 상태 publish 스케줄러 (동적 스케줄러 기반)
 * TaskScheduler 사용하여 런타임 중 주기 및 입장 수 변경 가능
 */
@Component
@RequiredArgsConstructor
public class QueueProcessorScheduler {

    private static long intervalMillis = 1000; // 주기(ms)
    private static int entryCount = 10; // 주기마다 입장할 인원 수
    private final QueueService queueService;
    private ScheduledFuture<?> scheduledTask;
    private final TaskScheduler taskScheduler;
    private final ExecutorConfig executorConfig;
    private final Environment environment;

    /**
     * 애플리케이션 시작 시 스케줄러 자동 시작
     */
    @PostConstruct
    public void init() {
        if (Arrays.asList(environment.getActiveProfiles()).contains("test")) {
            return; // test 프로필이면 scheduler 실행 안 함
        }

        startScheduler();
    }

    /**
     * 스케줄러 시작 (현재 설정된 주기 및 입장 수 기준)
     */
    public synchronized void startScheduler() {
        stopScheduler(); // 기존 스케줄러 제거
        scheduledTask = taskScheduler.scheduleAtFixedRate(this::process, Duration.ofMillis(intervalMillis));
    }

    /**
     * 스케줄러 중지
     */
    public synchronized void stopScheduler() {
        if (scheduledTask != null) {
            scheduledTask.cancel(false);
        }
    }

    /**
     * 스케줄러 주기 및 입장 수 동적 변경 (기존 스케줄러 갱신)
     */
    public synchronized void updateScheduler(long newIntervalMillis, int newEntryCount) {
        intervalMillis = newIntervalMillis;
        entryCount = newEntryCount;
        startScheduler();
    }

    /**
     * 입장 처리 및 대기열 상태 publish
     * 전체 대기열을 병렬로 처리
     */
    public void process() {
        // 전체 대기열 목록 조회
        List<Long> performanceSessionIds = queueService.getAllPerformanceSessionIds();

        // 각 대기열을 병렬로 처리
        performanceSessionIds.parallelStream().forEach(this::processQueue);
    }

    /**
     * 하나의 대기열에 대한 입장 처리와 상태 publish
     */
    private void processQueue(Long performanceSessionId) {
        // 입장할 인원 수만큼 대기열에서 제거
        List<String> connectionIds = queueService.pollTopCount(performanceSessionId, entryCount);

        // 각 인원을 비동기로 입장 처리
        connectionIds.forEach(connectionId ->
                CompletableFuture.runAsync(() -> processEntry(performanceSessionId, connectionId),
                        executorConfig.threadPoolTaskExecutor())
        );

        // 대기열 상태 publish
        publishWaitingState(performanceSessionId);
    }

    /**
     * 개별 사용자 입장 처리
     */
    private void processEntry(Long performanceSessionId, String connectionId) {
        // 입장 처리
        queueService.enterEntryLine(performanceSessionId, connectionId);
    }

    /**
     * 대기열 상태 publish
     */
    private void publishWaitingState(Long performanceSessionId) {
        // 대기열에 남아있는 전체 사용자 조회
        List<String> connectionIds = queueService.getLine(performanceSessionId);

        // 대기열 상태 publish를 비동기로 수행
        connectionIds.forEach(connectionId -> CompletableFuture.runAsync(() -> {
            // 대기열 상태 조회
            int position = queueService.getPosition(performanceSessionId, connectionId);
            int totalCount = queueService.getSize(performanceSessionId);

            // 대기열 상태 발행
            WaitingState waitingState = WaitingState.of(position, totalCount, getRps());
            queueService.publishWaitingState(performanceSessionId, connectionId, waitingState);
        }, executorConfig.threadPoolTaskExecutor()));
    }

    /**
     * 현재 RPS(초당 입장 처리 인원) 계산
     */
    public static double getRps() {
        return entryCount / ((double)intervalMillis / 1000);
    }
}
