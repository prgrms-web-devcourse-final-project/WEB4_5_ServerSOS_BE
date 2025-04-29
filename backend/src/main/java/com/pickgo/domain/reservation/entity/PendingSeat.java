package com.pickgo.domain.reservation.entity;

import com.pickgo.domain.area.seat.entity.Seat;
import com.pickgo.global.entity.BaseEntity;
import jakarta.persistence.*;

@Entity
public class PendingSeat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "seat_id", nullable = false, unique = true)
    private Seat seat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;
}
