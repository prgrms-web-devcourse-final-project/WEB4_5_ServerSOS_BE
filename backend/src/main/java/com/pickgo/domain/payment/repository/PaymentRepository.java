package com.pickgo.domain.payment.repository;

import com.pickgo.domain.member.entity.Member;
import com.pickgo.domain.payment.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Page<Payment> findByReservationMember(Member member, Pageable pageable);

    Optional<Payment> findByOrderId(String orderId);

}