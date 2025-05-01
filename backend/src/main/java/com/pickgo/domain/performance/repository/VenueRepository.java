package com.pickgo.domain.performance.repository;

import com.pickgo.domain.performance.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VenueRepository extends JpaRepository<Venue, Long> {
    Optional<Venue> findByNameAndAddress(String name, String address);
}
