package com.pickgo.domain.queue.dto;

import java.util.UUID;

import com.pickgo.global.sse.SseConnection;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * 대기열 관련 세션 정보
 */
@Getter
@SuperBuilder
public class QueueSession extends SseConnection {
    private final Long performanceSessionId;
    private final UUID userId;
}
