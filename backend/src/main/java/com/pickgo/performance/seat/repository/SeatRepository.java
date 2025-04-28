package com.pickgo.performance.seat.repository;

import com.pickgo.performance.seat.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {
}
