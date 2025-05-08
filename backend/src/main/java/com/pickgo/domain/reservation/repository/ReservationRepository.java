package com.pickgo.domain.reservation.repository;

import com.pickgo.domain.reservation.entity.Reservation;
import com.pickgo.domain.reservation.enums.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Page<Reservation> findByMemberId(UUID memberId, Pageable pageable);

    List<Reservation> findByStatusAndCreatedAtBefore(ReservationStatus status, LocalDateTime createdAtBefore);

    Page<Reservation> findByMemberIdAndStatusIn(UUID memberId, Collection<ReservationStatus> statuses,Pageable pageable);
}
