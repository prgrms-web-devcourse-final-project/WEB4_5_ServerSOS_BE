package com.pickgo.domain.performance.area.seat.event;

import com.pickgo.domain.performance.area.seat.entity.ReservedSeat;

public class SeatStatusChangedEvent {

    private final ReservedSeat seat;

    public SeatStatusChangedEvent(ReservedSeat seat) {
        this.seat = seat;
    }

    public ReservedSeat getSeat() {
        return seat;
    }
}
