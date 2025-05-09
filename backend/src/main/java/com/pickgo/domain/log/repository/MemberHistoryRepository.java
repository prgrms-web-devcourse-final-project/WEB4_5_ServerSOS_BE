package com.pickgo.domain.log.repository;

import com.pickgo.domain.log.entity.MemberHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberHistoryRepository extends JpaRepository<MemberHistory, Long> {
}
