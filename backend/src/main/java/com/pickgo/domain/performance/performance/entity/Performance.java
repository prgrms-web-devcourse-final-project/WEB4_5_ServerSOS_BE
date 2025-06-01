package com.pickgo.domain.performance.performance.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.pickgo.domain.performance.area.area.entity.PerformanceArea;
import com.pickgo.domain.performance.venue.entity.Venue;
import com.pickgo.global.entity.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
    @Enumerated(EnumType.STRING)
    private PerformanceType type;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
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
