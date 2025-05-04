package com.pickgo.domain.payment.entity;

import com.pickgo.domain.reservation.entity.Reservation;
import com.pickgo.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Builder
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status; // 결제 상태 (PENDING, COMPLETED, FAILED, CANCELLED)

    @Column(nullable = false)
    private Integer amount;
    
    private String paymentKey; // 토츠페이먼츠에서 발급해주는 키, 결제 승인 시 저장

    @Column(nullable = false, unique = true)
    private String orderId; // 주문 ID (UUID로 생성)

    @OneToOne
    @JoinColumn(name = "reservation_id", unique = true, nullable = false)
    private Reservation reservation;
}
