package com.pickgo.domain.area.seat.repository;

import com.pickgo.domain.area.seat.entity.ReservedSeat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservedSeatRepository extends JpaRepository<ReservedSeat, Long> {
}
