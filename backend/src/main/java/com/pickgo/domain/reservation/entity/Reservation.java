package com.pickgo.domain.reservation.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.pickgo.domain.member.member.entity.Member;
import com.pickgo.domain.performance.area.seat.entity.ReservedSeat;
import com.pickgo.domain.performance.performance.entity.PerformanceSession;
import com.pickgo.domain.reservation.enums.ReservationStatus;
import com.pickgo.global.entity.BaseEntity;
import com.pickgo.global.exception.BusinessException;
import com.pickgo.global.response.RsCode;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @Setter
    private Member member;

    @OnDelete(action = OnDeleteAction.CASCADE)
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
