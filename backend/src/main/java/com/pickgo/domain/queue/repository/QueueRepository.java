package com.pickgo.domain.queue.repository;

import java.util.List;

/**
 * 대기열 관련 Repository
 */
public interface QueueRepository {
    int add(Long performanceSessionId, String connectionId, String serverId);

    boolean isEmpty(Long performanceSessionId);

    int getSize(Long performanceSessionId);

    List<String> pollTopCount(Long performanceSessionId, int count);

    void remove(Long performanceSessionId, String connectionId);

    int getPosition(Long performanceSessionId, String connectionId);

    List<String> getAllKeys();

    List<String> getLine(Long performanceSessionId);

    void clearAll();

    boolean isIn(Long performanceSessionId, String connectionId);

    String getServerId(Long performanceSessionId, String connectionId);
}
