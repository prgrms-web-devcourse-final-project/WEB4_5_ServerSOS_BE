package com.pickgo.domain.performance.repository;

import com.pickgo.domain.performance.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue, Long> {
}
