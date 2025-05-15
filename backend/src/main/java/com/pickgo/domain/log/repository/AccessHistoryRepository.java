package com.pickgo.domain.log.repository;

import com.pickgo.domain.log.entity.AccessHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessHistoryRepository extends JpaRepository<AccessHistory, Long> {
}
