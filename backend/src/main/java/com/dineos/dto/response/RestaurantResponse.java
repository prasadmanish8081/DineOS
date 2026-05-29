package com.dineos.dto.response;

import java.time.Instant;

public record RestaurantResponse(
        Long id,
        String name,
        String slug,
        String address,
        String phone,
        String gstNumber,
        String logoUrl,
        Instant createdAt,
        Long ownerId
) {
}
