package com.pickgo.domain.performance.entity;

import com.pickgo.global.entity.BaseEntity;
import com.pickgo.domain.area.seat.entity.Seat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class PerformanceSession extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime performanceTime;

    @Column(nullable = false)
    private LocalDateTime reserveOpenAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id", nullable = false)
    private Performance performance;

    @Builder.Default
    @OneToMany(mappedBy = "performanceSession", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter
    private List<Seat> seats = new ArrayList<>();
}
