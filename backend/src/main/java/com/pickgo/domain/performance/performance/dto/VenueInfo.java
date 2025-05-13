package com.pickgo.domain.performance.performance.dto;
import com.pickgo.domain.performance.venue.entity.Venue;

public record VenueInfo(
        String name,
        String address
) {
    public static VenueInfo from(Venue venue) {
        return new VenueInfo(
                venue.getName(),
                venue.getAddress()
        );
    }
}
