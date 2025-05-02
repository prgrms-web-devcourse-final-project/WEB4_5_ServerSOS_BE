package com.pickgo.domain.area.area.entity;

import com.pickgo.global.entity.BaseEntity;
import com.pickgo.domain.performance.entity.Performance;
import com.pickgo.domain.area.seat.entity.Seat;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class PerformanceArea extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AreaName name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AreaGrade grade;

    @Column(nullable = false)
    @Setter
    private Integer price;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id", nullable = false)
    private Performance performance;

    @Builder.Default
    @OneToMany(mappedBy = "performanceArea", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter
    private List<Seat> seats = new ArrayList<>();
}
