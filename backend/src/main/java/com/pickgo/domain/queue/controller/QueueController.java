package com.pickgo.domain.queue.controller;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.pickgo.domain.member.member.dto.MemberPrincipal;
import com.pickgo.domain.queue.dto.QueueSession;
import com.pickgo.domain.queue.service.QueueService;
import com.pickgo.global.sse.SseHandler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/queue")
@RequiredArgsConstructor
@Tag(name = "Queue API", description = "Queue API 엔드포인트")
public class QueueController {

    private final QueueService queueService;
    private final SseHandler sseHandler;

    @Operation(summary = "공연 예매 대기열 입장 및 구독")
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(
            @AuthenticationPrincipal MemberPrincipal principal,
            @RequestParam("sessionId") Long performanceSessionId
    ) {
        // SSE 세션 정보 생성 
        QueueSession session = queueService.genQueueSession(performanceSessionId, principal.id(),
                sseHandler.genConnectionId());

        // 대기열 입장
        queueService.enterWaitingLine(performanceSessionId, session.getConnectionId());

        // SSE 구독 및 emitter 반환
        return sseHandler.subscribe(session, () -> {
            sseHandler.removeEmitter(session.getConnectionId());
            queueService.remove(performanceSessionId, session.getConnectionId());
        }).getEmitter();
    }
}
