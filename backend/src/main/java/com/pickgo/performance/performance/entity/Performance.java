package com.pickgo.performance.performance.entity;

import com.pickgo.global.entity.BaseEntity;
import com.pickgo.performance.area.entity.PerformanceArea;
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
    @Setter
    private String name;

    @Column(nullable = false)
    @Setter
    private LocalDate startDate;

    @Column(nullable = false)
    @Setter
    private LocalDate endDate;

    @Column(nullable = false)
    @Setter
    private Integer runtime;

    @Column(nullable = false)
    @Setter
    private String poster;

    @Column(nullable = false)
    @Setter
    private String state;

    @Column(nullable = false)
    @Setter
    private Integer minAge;

    @Column(nullable = false)
    @Setter
    private String casts;

    @Column(nullable = false)
    @Setter
    private String productionCompany;

    @Column(nullable = false)
    @Setter
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    @Setter
    private Venue venue;

    @Builder.Default
    @OneToMany(mappedBy = "performance", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PerformanceIntro> performanceIntros = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "performance", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PerformanceArea> performanceAreas = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "performance", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PerformanceSession> performanceSessions = new ArrayList<>();
}