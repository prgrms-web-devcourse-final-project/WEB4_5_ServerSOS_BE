package com.pickgo.domain.performance.dto;
import com.pickgo.domain.venue.entity.Venue;

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
