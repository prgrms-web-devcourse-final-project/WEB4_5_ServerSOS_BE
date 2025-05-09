package com.pickgo.domain.area.seat.entity;

import com.pickgo.domain.area.area.entity.PerformanceArea;
import com.pickgo.domain.performance.entity.PerformanceSession;
import com.pickgo.domain.reservation.entity.Reservation;
import com.pickgo.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@Table(
        name = "reserved_seat",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_reserved_seat_area_row_number",
                        columnNames = {"performance_area_id", "seat_row", "number"}
                )
        }
)
public class ReservedSeat extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "seat_row")
    private String row;

    @Column(nullable = false)
    private Integer number;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Setter
    private SeatStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    @Setter
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_area_id", nullable = false)
    private PerformanceArea performanceArea;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_session_id", nullable = false)
    @Setter
    private PerformanceSession performanceSession;
}
