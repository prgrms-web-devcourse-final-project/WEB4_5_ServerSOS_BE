package com.pickgo.domain.performance.entity;

import com.pickgo.global.entity.BaseEntity;
import com.pickgo.domain.area.area.entity.PerformanceArea;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class Performance extends BaseEntity {
    @Id
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
    private List<PerformanceIntro> performanceIntros = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "performance", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter
    private List<PerformanceArea> performanceAreas = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "performance", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter
    private List<PerformanceSession> performanceSessions = new ArrayList<>();
}