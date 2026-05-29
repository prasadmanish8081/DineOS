package com.dineos.dto.response;

import java.time.Instant;

public record CategoryResponse(
        Long id,
        String name,
        Long restaurantId,
        Instant createdAt
) {
}
