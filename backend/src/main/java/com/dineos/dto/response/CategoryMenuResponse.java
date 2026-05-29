package com.dineos.dto.response;

import java.time.Instant;
import java.util.List;

public record CategoryMenuResponse(
        Long id,
        String name,
        Long restaurantId,
        Instant createdAt,
        List<MenuItemResponse> items
) {
}
