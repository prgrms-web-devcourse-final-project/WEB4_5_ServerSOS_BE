package com.pickgo.domain.area.seat.controller;

import com.pickgo.domain.area.seat.dto.SeatResponse;
import com.pickgo.domain.area.seat.dto.SeatUpdateRequest;
import com.pickgo.domain.area.seat.service.SeatService;
import com.pickgo.global.response.RsCode;
import com.pickgo.global.response.RsData;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

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

    @Operation(summary = "좌석 목록 조회")
    @GetMapping("/{areaId}/seats")
    public RsData<List<SeatResponse>> getSeats(
            @PathVariable Long areaId,
            @RequestParam Long sessionId) {
        List<SeatResponse> seats = seatService.getSeats(areaId, sessionId);
        return new RsData<>(RsCode.SUCCESS.getCode(), "좌석 목록을 조회하였습니다.", seats);
    }

    /**
     * 좌석 상태 변경 알림 전송 API
     * POST /api/seats/update?sessionId=1
     * Body (JSON):
     * {
     *   "seatId": 123,
     *   "status": "RESERVED"
     * }
     */
    @Operation(summary = "좌석 상태 변경")
    @PostMapping("/update-status")
    public RsData<?> updateSeatStatus(@RequestBody SeatUpdateRequest request) {
        seatService.updateSeatStatus(request);
        return RsData.from(RsCode.SUCCESS);
    }
}
