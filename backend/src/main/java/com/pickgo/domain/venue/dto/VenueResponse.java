package com.pickgo.domain.venue.dto;

import com.pickgo.domain.venue.entity.Venue;

public record VenueResponse(
        String name,
        String address
) {
    public static VenueResponse from(Venue venue) {
        return new VenueResponse(
                venue.getName(),
                venue.getAddress()
        );
    }
}
