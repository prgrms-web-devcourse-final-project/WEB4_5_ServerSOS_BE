package com.pickgo.domain.area.seat.repository;

import com.pickgo.domain.area.seat.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {
}
