package com.pickgo.domain.area.seat.controller;

import com.pickgo.domain.area.seat.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatUpdateController { //Post맨으로 테스트 확인하기위해 임시로 만든 컨트롤러 , 이부분 컨트롤러는 프론트 단에서 만들어지면 삭제할 예정

    private final SeatService seatService;

    /**
     * 테스트용: 좌석 상태 변경 알림 전송 API
     * POST /api/seats/update?sessionId=1
     * Body (JSON):
     * {
     *   "seatId": 123,
     *   "status": "RESERVED"
     * }
     */
    @PostMapping("/update")
    public ResponseEntity<Void> updateSeatStatus(
            @RequestParam Long sessionId,
            @RequestBody Map<String, Object> seatUpdateData
    ) {
        seatService.notifySeatUpdate(sessionId, seatUpdateData);
        return ResponseEntity.ok().build();
    }
}