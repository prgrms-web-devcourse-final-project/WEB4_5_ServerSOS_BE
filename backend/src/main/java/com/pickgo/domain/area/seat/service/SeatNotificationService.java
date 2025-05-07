package com.pickgo.domain.area.seat.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class SeatNotificationService {
    //공연 세션 ID별로 SSE 구독자 리스트 저장
    private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();
    // SSE 연결 타임아웃: 30분
    private static final Long TIMEOUT = 30L * 60 * 1000L;

    /**
     * 클라이언트가 특정 세션의 좌석 상태를 실시간으로 구독할 때 호출
     * @param sessionId 공연 세션 ID
     * @return SseEmitter (서버 → 클라이언트로 데이터 전송하는 스트림 객체)
     */
    public SseEmitter subscribe(Long sessionId) {
        // 새 SSE 연결 생성
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        // 해당 세션에 구독자 리스트 없으면 새로 생성하고 emitter 추가
        emitters.computeIfAbsent(sessionId, key -> new ArrayList<>()).add(emitter);

        log.info("New SSE subscription for sessionId = {}", sessionId);
        //타임아웃 시 : emitter 제거함
        emitter.onTimeout(() -> {
            log.warn("SSE Timeout: sessionId = {}", sessionId);
            removeEmitter(sessionId, emitter);
        });
        //연결 완료 시: emitter 제거함
        emitter.onCompletion(() -> {
            log.info("SSE Completed: sessionId = {}", sessionId);
            removeEmitter(sessionId, emitter);
        });
        //연결 테스트용 초기 메시지 전송
        try {
            emitter.send(SseEmitter.event().name("INIT").data("connected"));
        } catch (IOException e) {
            log.error("SSE INIT 전송 실패", e);
            emitter.completeWithError(e); //오류 발생하면 emitter 종료
        }

        return emitter;
    }

    /**
     * 특정 세션의 모든 구독자에게 좌석 상태 변경 이벤트를 전송
     * @param sessionId 공연 세션 ID
     * @param seatUpdateData 전송할 좌석 상태 데이터
     */
    public void notifySeatUpdate(Long sessionId, Object seatUpdateData) {
        // 해당 세션의 emitter 리스트 가져오기 (없으면 빈 리스트)
        List<SseEmitter> sessionEmitters = emitters.getOrDefault(sessionId, new ArrayList<>());

        // 전송 실패한 emitter들을 따로 저장해 나중에 제거
        List<SseEmitter> deadEmitters = new ArrayList<>();

        for (SseEmitter emitter : sessionEmitters) {
            try {
                // 각 구독자에게 seat-update 이벤트 전송
                emitter.send(SseEmitter.event().name("seat-update").data(seatUpdateData));
            } catch (IOException e) {
                // 전송 실패 시 emitter 종료 및 dead 리스트에 추가
                emitter.completeWithError(e);
                deadEmitters.add(emitter);
            }
        }

        // 실패한 emitter들 목록에서 제거
        sessionEmitters.removeAll(deadEmitters);
    }

    /**
     * Emitter를 세션별 리스트에서 제거하는 내부 메서드
     * @param sessionId 공연 세션 ID
     * @param emitter 제거할 SSE emitter
     */
    private void removeEmitter(Long sessionId, SseEmitter emitter) {
        List<SseEmitter> sessionEmitters = emitters.get(sessionId);
        if (sessionEmitters != null) {
            sessionEmitters.remove(emitter);
        }
    }
}