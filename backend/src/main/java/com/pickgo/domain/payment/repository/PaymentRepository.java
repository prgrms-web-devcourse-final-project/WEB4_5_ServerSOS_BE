package com.pickgo.domain.payment.repository;

import com.pickgo.domain.member.entity.Member;
import com.pickgo.domain.payment.entity.Payment;
import com.pickgo.domain.payment.entity.PaymentStatus;
import com.pickgo.domain.reservation.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Page<Payment> findByReservationMember(Member member, Pageable pageable);

    Optional<Payment> findByReservation(Reservation reservation);

    boolean existsByReservation(Reservation reservation);

    List<Payment> findByStatusAndCreatedAtBefore(PaymentStatus paymentStatus, LocalDateTime createdAtBefore);
}