package com.pickgo.global.sse;

import java.io.IOException;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SSE Handler
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SseHandler {

    private final static long TIMEOUT_MILLIS = -1L; // Emitter 연결 지속 시간 (무제한)
    private final SseConnectionRegistry sseConnectionRegistry;

    /**
     * SSE 구독 시작 (Emitter 생성 및 등록)
     * - connectionId로 연결 정보 등록
     * - 연결 종료/타임아웃/에러 발생 시 cleanup 실행
     */
    public <T extends SseConnection> T subscribe(T connection, Runnable cleanup) {
        SseEmitter emitter = new SseEmitter(TIMEOUT_MILLIS);
        connection.setEmitter(emitter);

        sseConnectionRegistry.add(connection.getConnectionId(), connection);

        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(e -> {
            log.warn("[SSE] 연결 끊김 또는 예외 - connectionId: {}", connection.getConnectionId(), e);
            cleanup.run();
        });

        return connection;
    }

    /**
     * connectionId로 SSE 연결 정보 조회
     */
    @SuppressWarnings("unchecked")
    public <T extends SseConnection> T getSession(String connectionId, Class<T> type) {
        SseConnection connection = sseConnectionRegistry.getConnection(connectionId);
        if (connection == null) {
            return null;
        }

        if (!type.isInstance(connection)) {
            log.warn("Invalid connection type - connectionId: {}", connectionId);
            return null;
        }

        return (T)connection;
    }

    /**
     * connectionId로 등록된 SseEmitter 조회
     */
    public SseEmitter getEmitter(String connectionId) {
        return sseConnectionRegistry.getConnection(connectionId).getEmitter();
    }

    /**
     * connectionId로 등록된 Emitter 및 연결 정보 제거
     */
    public void removeEmitter(String connectionId) {
        sseConnectionRegistry.remove(connectionId);
    }

    /**
     * 지정한 connectionId로 SSE 메시지 전송
     * @param eventName SSE 이벤트 이름
     * @param data 전송할 데이터
     */
    public <T> void sendMessage(String eventName, String connectionId, T data) throws IOException {
        SseEmitter emitter = getEmitter(connectionId);

        emitter.send(
                SseEmitter.event()
                        .name(eventName)
                        .data(data)
        );
    }

    /**
     * 지정한 connectionId의 SSE 연결 종료
     */
    public void complete(String connectionId) {
        SseEmitter emitter = getEmitter(connectionId);
        if (emitter != null) {
            emitter.complete();
        }
    }

    /**
     * 새로운 connectionId(UUID)를 생성
     */
    public String genConnectionId() {
        return UUID.randomUUID().toString();
    }
}
