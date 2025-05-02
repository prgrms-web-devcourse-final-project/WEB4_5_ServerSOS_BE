package com.pickgo.domain.reservation.repository;

import com.pickgo.domain.reservation.entity.PendingSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PendingSeatRepository extends JpaRepository<PendingSeat, Long> {
}
