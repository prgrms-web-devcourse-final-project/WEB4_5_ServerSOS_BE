package com.pickgo.domain.area.seat.controller;

import com.pickgo.domain.area.seat.dto.SeatUpdateRequest;
import com.pickgo.domain.area.seat.service.SeatService;
import com.pickgo.global.response.RsCode;
import com.pickgo.global.response.RsData;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatUpdateController {

    private final SeatService seatService;

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
        seatService.updateSeatStatus(request.seatId(), request.status());
        return RsData.from(RsCode.SUCCESS);
    }
}


