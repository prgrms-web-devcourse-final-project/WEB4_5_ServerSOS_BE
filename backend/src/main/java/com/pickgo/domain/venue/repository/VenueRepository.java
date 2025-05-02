package com.pickgo.domain.venue.repository;

import com.pickgo.domain.venue.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VenueRepository extends JpaRepository<Venue, Long> {
    Optional<Venue> findByNameAndAddress(String name, String address);
}
