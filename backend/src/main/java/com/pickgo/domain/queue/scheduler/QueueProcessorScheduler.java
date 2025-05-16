package com.pickgo.domain.queue.scheduler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.pickgo.domain.queue.dto.QueueSession;
import com.pickgo.domain.queue.dto.WaitingState;
import com.pickgo.domain.queue.service.QueueService;
import com.pickgo.global.sse.SseHandler;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

/**
 * 대기열 입장 처리 및 상태 브로드캐스트 스케줄러 (동적 스케줄러 기반)
 * TaskScheduler 사용하여 런타임 중 주기 및 입장 수 변경 가능
 */
@Component
@RequiredArgsConstructor
public class QueueProcessorScheduler {

    private final QueueService queueService;
    private final SseHandler sseHandler;
    private final TaskScheduler taskScheduler;

    private final Map<Long, Object> locks = new ConcurrentHashMap<>();
    private ScheduledFuture<?> scheduledTask;

    private static long intervalMillis = 500; // 주기(ms)
    private static int entryCount = 1; // 주기마다 입장할 인원 수


    /**
     * 애플리케이션 시작 시 스케줄러 자동 시작
     */
    @PostConstruct
    public void init() {
        startScheduler();
    }

    /**
     * 스케줄러 시작 (현재 설정된 주기 및 입장 수 기준)
     */
    public synchronized void startScheduler() {
        stopScheduler(); // 기존 스케줄러 제거
        scheduledTask = taskScheduler.scheduleAtFixedRate(this::process, intervalMillis);
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
        this.intervalMillis = newIntervalMillis;
        this.entryCount = newEntryCount;
        startScheduler();
    }

    /**
     * 입장 처리 및 대기열 상태 브로드캐스트
     * 전체 대기열을 병렬로 처리
     */
    public void process() {
        // 전체 대기열 목록 조회
        List<Long> performanceSessionIds = queueService.getAllPerformanceSessionIds();

        // 각 대기열을 병렬로 처리
        performanceSessionIds.parallelStream().forEach(this::processQueue);
    }

    /**
     * 하나의 대기열에 대한 입장 처리와 상태 브로드캐스트
     */
    private void processQueue(Long performanceSessionId) {
        List<String> connectionIds;

        // performanceSessionId 단위로 락을 관리
        synchronized (locks.computeIfAbsent(performanceSessionId, id -> new Object())) {
            // 입장 대상 인원 조회 및 대기열에서 제거
            connectionIds = queueService.pollTopCount(performanceSessionId, entryCount);
        }

        // 각 인원을 비동기로 입장 처리
        List<CompletableFuture<Void>> futures = connectionIds.stream()
                .map(connectionId -> CompletableFuture.runAsync(() -> processEntry(performanceSessionId, connectionId)))
                .toList();

        // 모든 입장 처리가 끝날 때까지 대기
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // 대기열 상태 브로드캐스트
        waitingStateBroadCast(performanceSessionId);
    }

    /**
     * 개별 사용자 입장 처리
     */
    private void processEntry(Long performanceSessionId, String connectionId) {
        // 사용자 세션 조회
        QueueSession session = sseHandler.getSession(connectionId, QueueSession.class);
        if (session == null)
            return;

        // 입장 처리
        queueService.enterEntryLine(performanceSessionId, session.getUserId(), session.getConnectionId());
    }

    /**
     * 대기열 상태 브로드캐스트
     */
    private void waitingStateBroadCast(Long performanceSessionId) {
        // 대기열에 남아있는 전체 사용자 조회
        List<String> connectionIds = queueService.getLine(performanceSessionId);

        // 대기열 상태 브로드캐스트를 비동기로 수행
        connectionIds.forEach(connectionId -> CompletableFuture.runAsync(() -> {
            // 대기열 상태 조회
            int position = queueService.getPosition(performanceSessionId, connectionId);
            int totalCount = queueService.getSize(performanceSessionId);

            // TPS 기준 ETA 계산
            WaitingState waitingState = WaitingState.of(position, totalCount, getTps());
            queueService.publishWaitingState(performanceSessionId, connectionId, waitingState);
        }));
    }

    /**
     * 현재 TPS(초당 처리 인원) 계산
     */
    public static double getTps() {
        return entryCount / ((double)intervalMillis / 1000);
    }
}
