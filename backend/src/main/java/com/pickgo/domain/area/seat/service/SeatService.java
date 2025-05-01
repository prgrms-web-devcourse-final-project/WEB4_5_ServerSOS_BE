package com.pickgo.domain.area.seat.service;

import com.pickgo.domain.area.seat.dto.SeatResponse;
import com.pickgo.domain.area.seat.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;

    // 공연 세션별 구독자 목록
    private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    // 기본 타임아웃 (30분)
    private static final Long TIMEOUT = 30L * 60 * 1000L;


    /**
     * SSE 구독 요청
     */
    public SseEmitter subscribe(Long sessionId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT);

        // emitters 맵에 구독자 리스트가 없으면 새로 생성
        emitters.computeIfAbsent(sessionId, key -> new ArrayList<>()).add(emitter);

        log.info("New SSE subscription for sessionId = {}", sessionId);

        // 연결 끊김 또는 타임아웃 처리
        emitter.onTimeout(() -> {
            log.warn("SSE Timeout: sessionId = {}", sessionId);
            removeEmitter(sessionId, emitter);
        });

        emitter.onCompletion(() -> {
            log.info("SSE Completed: sessionId = {}", sessionId);
            removeEmitter(sessionId, emitter);
        });

        // 연결 확인용 더미 이벤트 전송
        try {
            emitter.send(SseEmitter.event().name("INIT").data("connected"));
        } catch (IOException e) {
            log.error("SSE INIT 전송 실패", e);
            emitter.completeWithError(e);
        }

        return emitter;
    }

    /**
     * 특정 세션 구독자들에게 좌석 상태 변경 알림 전송
     */
    public void notifySeatUpdate(Long sessionId, Object seatUpdateData) {
        List<SseEmitter> sessionEmitters = emitters.getOrDefault(sessionId, new ArrayList<>());
        List<SseEmitter> deadEmitters = new ArrayList<>();
        for (SseEmitter emitter : sessionEmitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("seat-update")
                        .data(seatUpdateData));
            } catch (IOException e) {
                emitter.completeWithError(e);
                deadEmitters.add(emitter); //실패한 emitter를 모아서
            }
        }

        // 완료되거나 오류 난 emitter는 제거
        sessionEmitters.removeAll(deadEmitters);
    }

    private void removeEmitter(Long sessionId, SseEmitter emitter) {
        List<SseEmitter> sessionEmitters = emitters.get(sessionId);
        if (sessionEmitters != null) {
            sessionEmitters.remove(emitter);
        }
    }

    public List<SeatResponse> getSeats(Long areaId, Long seesionId) {
        return seatRepository.findByPerformanceAreaIdAndPerformanceSessionId(areaId, seesionId)
                .stream()
                .map(SeatResponse::from)
                .collect(Collectors.toList());


    }
}
