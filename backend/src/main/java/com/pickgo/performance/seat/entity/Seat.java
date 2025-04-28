package com.pickgo.performance.seat.entity;

import com.pickgo.global.entity.BaseEntity;
import com.pickgo.performance.area.entity.PerformanceArea;
import com.pickgo.performance.performance.entity.PerformanceSession;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class Seat extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "seat_row")
    @Setter
    private String row;

    @Column(nullable = false)
    @Setter
    private Integer number;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Setter
    private SeatStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_session_id", nullable = false)
    @Setter
    private PerformanceSession performanceSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_area_id", nullable = false)
    @Setter
    private PerformanceArea performanceArea;
}
