package com.pickgo.domain.performance.area.seat.controller;

import com.pickgo.domain.performance.area.seat.service.SeatService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/areas")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    /**
     * 공연 세션에 해당하는 좌석 상태를 실시간으로 구독
     * 클라이언트에서 EventSource("/api/areas/subscribe?sessionId=1") 식으로 요청
     */

    @Operation(summary = "좌석 상태 실시간 구독")
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@RequestParam Long sessionId) {
        return seatService.subscribeToSeatUpdates(sessionId);
    }




}
