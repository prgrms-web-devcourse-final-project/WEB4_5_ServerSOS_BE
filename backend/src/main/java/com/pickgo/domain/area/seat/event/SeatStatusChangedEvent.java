package com.pickgo.domain.area.seat.event;

import com.pickgo.domain.area.seat.entity.ReservedSeat;

public class SeatStatusChangedEvent {

    private final ReservedSeat seat;

    public SeatStatusChangedEvent(ReservedSeat seat) {
        this.seat = seat;
    }
}
