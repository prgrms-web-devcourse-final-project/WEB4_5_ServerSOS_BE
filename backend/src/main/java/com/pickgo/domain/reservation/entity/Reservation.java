package com.pickgo.domain.reservation.entity;

import com.pickgo.domain.area.seat.entity.ReservedSeat;
import com.pickgo.domain.area.seat.entity.SeatStatus;

import com.pickgo.domain.member.entity.Member;
import com.pickgo.domain.performance.entity.PerformanceSession;
import com.pickgo.domain.reservation.enums.ReservationStatus;
import com.pickgo.global.entity.BaseEntity;
import com.pickgo.global.exception.BusinessException;
import com.pickgo.global.response.RsCode;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private Integer totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @Setter
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private PerformanceSession performanceSession;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReservedSeat> reservedSeats = new ArrayList<>();

    public void updateReservedSeats(List<ReservedSeat> seats) {
        this.reservedSeats.clear();
        this.reservedSeats.addAll(seats);
    }

    public void cancel() {
        if (this.status != ReservationStatus.PAID) {
            throw new BusinessException(RsCode.INVALID_RESERVATION_STATE);
        }
        this.status = ReservationStatus.CANCELED;
    }

}
