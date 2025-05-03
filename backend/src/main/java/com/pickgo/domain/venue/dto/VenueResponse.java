package com.pickgo.domain.venue.dto;

import com.pickgo.domain.venue.entity.Venue;

public record VenueResponse(
        Long id,
        String name,
        String address
) {
    public static VenueResponse from(Venue venue) {
        return new VenueResponse(
                venue.getId(),
                venue.getName(),
                venue.getAddress()
        );
    }
}
