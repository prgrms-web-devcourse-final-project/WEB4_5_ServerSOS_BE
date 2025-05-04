package com.pickgo.domain.payment.entity;

import com.pickgo.domain.reservation.entity.Reservation;
import com.pickgo.global.entity.BaseEntity;
import com.pickgo.global.exception.BusinessException;
import com.pickgo.global.response.RsCode;
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

    @OneToOne
    @JoinColumn(name = "reservation_id", unique = true, nullable = false)
    private Reservation reservation;

    public void cancel() {
        if (this.status != PaymentStatus.COMPLETED) {
            throw new BusinessException(RsCode.INVALID_PAYMENT_STATE);
        }
        this.status = PaymentStatus.CANCELLED;
    }
}
