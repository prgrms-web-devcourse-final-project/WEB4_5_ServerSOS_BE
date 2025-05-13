package com.pickgo.domain.area.seat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatNotificationService seatNotificationService;

    public SseEmitter subscribeToSeatUpdates(Long sessionId) {

        return seatNotificationService.subscribe(sessionId);
    }
}