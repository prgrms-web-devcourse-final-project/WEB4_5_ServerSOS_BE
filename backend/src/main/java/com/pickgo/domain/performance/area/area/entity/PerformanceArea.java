package com.pickgo.domain.performance.area.area.entity;

import com.pickgo.domain.performance.performance.entity.Performance;
import com.pickgo.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(nullable = false)
    private Integer rowCount;

    @Column(nullable = false)
    private Integer colCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id", nullable = false)
    private Performance performance;
}
