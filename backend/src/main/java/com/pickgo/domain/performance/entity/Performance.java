package com.pickgo.domain.performance.entity;

import com.pickgo.domain.area.area.entity.PerformanceArea;
import com.pickgo.domain.venue.entity.Venue;
import com.pickgo.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class Performance extends BaseEntity {
    @Id
    @Column(name = "performance_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private String runtime;

    @Column(nullable = false)
    private String poster;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Setter
    private PerformanceState state;

    @Column(nullable = false)
    private String minAge;

    @Column(nullable = false)
    private String casts;

    @Column(nullable = false)
    private String productionCompany;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PerformanceType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id")
    private Venue venue;

    @Builder.Default
    @OneToMany(mappedBy = "performance", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private Set<PerformanceIntro> performanceIntros = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "performance", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter
    @BatchSize(size = 10)
    private List<PerformanceArea> performanceAreas = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "performance", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter
    @BatchSize(size = 10)
    private List<PerformanceSession> performanceSessions = new ArrayList<>();
}
