package com.pickgo.domain.venue.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "venue_id")
    private Long venueId;

    private String name;

    private String address;

    private LocalDateTime created_At;

    private LocalDateTime modified_At;

}
