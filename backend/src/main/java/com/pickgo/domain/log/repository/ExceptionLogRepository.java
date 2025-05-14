package com.pickgo.domain.log.repository;

import com.pickgo.domain.log.entity.ExceptionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExceptionLogRepository extends JpaRepository<ExceptionHistory, Long> {
}
