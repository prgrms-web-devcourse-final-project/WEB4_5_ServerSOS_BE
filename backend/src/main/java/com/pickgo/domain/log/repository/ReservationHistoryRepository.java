package com.pickgo.domain.log.repository;

import com.pickgo.domain.log.entity.ReservationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationHistoryRepository extends JpaRepository<ReservationHistory, Long> {
}
