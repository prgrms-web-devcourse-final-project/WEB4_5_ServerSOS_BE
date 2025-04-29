package com.pickgo.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Performance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long performanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id")
    private Venue venueId;

    private String name;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private int runtime;

    private String poster;

    private String state;

    @Enumerated(EnumType.STRING)
    private PerformanceType type; //ENUM 타입

    private int minAge;

    private String casts;

    private String productionCompany;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
