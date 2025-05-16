package com.pickgo.global.infra.server;

/**
 * 서버 식별자 저장소
 */
public interface ServerRegistry {
    void save(String connectionId, String serverId);

    String getServerId(String connectionId);

    void remove(String connectionId);

    void clearAll();
}
