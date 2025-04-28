package com.pickgo.domain.area.seat.entity;

import com.pickgo.global.entity.BaseEntity;
import com.pickgo.domain.area.area.entity.PerformanceArea;
import com.pickgo.domain.performance.entity.PerformanceSession;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Builder
public class Seat extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "seat_row")
    private String row;

    @Column(nullable = false)
    private Integer number;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_session_id", nullable = false)
    private PerformanceSession performanceSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_area_id", nullable = false)
    private PerformanceArea performanceArea;
}
