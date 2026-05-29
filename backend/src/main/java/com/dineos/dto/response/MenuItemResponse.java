package com.dineos.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record MenuItemResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String imageUrl,
        Boolean isVeg,
        Boolean isAvailable,
        Integer spicyLevel,
        Integer preparationTime,
        Long categoryId,
        String categoryName,
        Long restaurantId,
        Instant createdAt
) {
}
