package com.pickgo.performance.performance.repository;

import com.pickgo.performance.performance.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue, Long> {
}
