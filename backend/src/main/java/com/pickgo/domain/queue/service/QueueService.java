package com.pickgo.domain.queue.service;

import static com.pickgo.domain.queue.scheduler.QueueProcessorScheduler.*;
import static com.pickgo.domain.queue.stream.QueueStreamConsumer.*;
import static com.pickgo.global.response.RsCode.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.pickgo.domain.queue.dto.QueueSession;
import com.pickgo.domain.queue.dto.WaitingState;
import com.pickgo.domain.queue.repository.QueueRepository;
import com.pickgo.global.exception.BusinessException;
import com.pickgo.global.infra.server.ServerRegistry;
import com.pickgo.global.infra.stream.redis.RedisStreamPublisher;
import com.pickgo.global.init.ServerIdProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QueueService {

    private final ServerRegistry serverRegistry;
    private final QueueRepository queueRepository;
    private final RedisStreamPublisher redisStreamPublisher;
    private final ServerIdProvider serverIdProvider;

    /**
     * 대기열 입장
     */
    public void enterWaitingLine(Long performanceSessionId, String connectionId) {
        // 대기열 추가 및 상태 조회
        int position = queueRepository.add(performanceSessionId, connectionId, serverIdProvider.getServerId());
        int totalCount = queueRepository.getSize(performanceSessionId);
        double rps = getRps();

        // 연결된 서버 저장
        serverRegistry.save(connectionId, serverIdProvider.getServerId());

        // 대기열 상태 publish
        WaitingState waitingState = WaitingState.of(position, totalCount, rps);
        publishWaitingState(performanceSessionId, connectionId, waitingState);
    }

    /**
     * 입장큐 입장
     */
    public void enterEntryLine(Long performanceSessionId, String connectionId) {
        publishEntryPermission(performanceSessionId, connectionId);
    }

    /**
     * 대기열 상태 publish
     */
    public void publishWaitingState(Long performanceSessionId, String connectionId, WaitingState waitingState) {
        redisStreamPublisher.publish(getStreamKey(connectionId),
                genStreamData(performanceSessionId, connectionId, waitingState));
    }

    /**
     * 입장 메시지 publish
     */
    public void publishEntryPermission(Long performanceSessionId, String connectionId) {
        redisStreamPublisher.publish(getStreamKey(connectionId),
                genStreamData(performanceSessionId, connectionId));
    }

    /**
     * 대기열 비어있는지 여부
     */
    public boolean isEmpty(Long performanceSessionId) {
        return queueRepository.isEmpty(performanceSessionId);
    }

    /**
     * 대기열에서 count만큼 앞에서 꺼냄
     */
    public List<String> pollTopCount(Long performanceSessionId, int count) {
        return queueRepository.pollTopCount(performanceSessionId, count);
    }

    /**
     * 특정 커넥션을 대기열에서 제거
     */
    public void remove(Long performanceSessionId, String connectionId) {
        queueRepository.remove(performanceSessionId, connectionId);
    }

    /**
     * 대기 번호 조회
     */
    public int getPosition(Long performanceSessionId, String connectionId) {
        return queueRepository.getPosition(performanceSessionId, connectionId);
    }

    /**
     * 대기열 입장한 전체 인원 수
     */
    public int getSize(Long performanceSessionId) {
        return queueRepository.getSize(performanceSessionId);
    }

    /**
     * 대기열이 활성화된 모든 공연 세션 아이디 조회
     */
    public List<Long> getAllPerformanceSessionIds() {
        return queueRepository.getAllKeys().stream()
                .map(this::extractedPerformanceSessionId)
                .toList();
    }

    /**
     * 특정 대기열 전체 조회
     */
    public List<String> getLine(Long performanceSessionId) {
        return queueRepository.getLine(performanceSessionId);
    }

    /**
     * 모든 대기열 초기화
     */
    public void clearAll() {
        queueRepository.clearAll();
    }

    /**
     * 특정 커넥션이 대기열에 존재하는지 확인
     */
    public boolean isIn(Long performanceSessionId, String connectionId) {
        return queueRepository.isIn(performanceSessionId, connectionId);
    }

    /**
     * key에서 performance session id 추출
     */
    private Long extractedPerformanceSessionId(String key) {
        final String marker = "performance_session_id:";
        int index = key.indexOf(marker);
        if (index == -1) {
            throw new BusinessException(INTERNAL_SERVER);
        }
        return Long.valueOf(key.substring(index + marker.length()));
    }

    /**
     * Stream에 publish할 data 생성 (대기열 상태)
     */
    private static Map<String, String> genStreamData(Long performanceSessionId, String connectionId, WaitingState waitingState) {
        Map<String, String> data = new HashMap<>();
        data.put("type", "wait");
        data.put("performance_session_id", performanceSessionId.toString());
        data.put("connection_id", connectionId);
        data.put("position", String.valueOf(waitingState.position()));
        data.put("total_count", String.valueOf(waitingState.totalCount()));
        data.put("estimated_time", String.valueOf(waitingState.estimatedTime()));
        return data;
    }

    /**
     * Stream에 publish할 data 생성 (입장 메시지)
     */
    private Map<String, String> genStreamData(Long performanceSessionId, String connectionId) {
        Map<String, String> data = new HashMap<>();
        data.put("type", "ready");
        data.put("performance_session_id", performanceSessionId.toString());
        data.put("connection_id", connectionId);
        return data;
    }

    /**
     * 대기열 세션 정보 생성
     */
    public QueueSession genQueueSession(Long performanceSessionId, UUID userId, String connectionId) {
        return QueueSession.builder()
                .performanceSessionId(performanceSessionId)
                .userId(userId)
                .serverId(serverIdProvider.getServerId())
                .connectionId(connectionId)
                .build();
    }

    /**
     * 대기열 Stream 키
     */
    public String getStreamKey(String connectionId) {
        String serverId = serverRegistry.getServerId(connectionId);
        return QUEUE_STREAM_PREFIX + ":server_id:" + serverId;
    }
}
