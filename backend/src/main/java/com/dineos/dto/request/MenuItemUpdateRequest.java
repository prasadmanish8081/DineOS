package com.dineos.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record MenuItemUpdateRequest(
        @NotBlank(message = "Menu item name is required")
        @Size(min = 2, max = 150, message = "Menu item name must be between 2 and 150 characters")
        String name,

        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        BigDecimal price,

        @Size(max = 500, message = "Image URL must not exceed 500 characters")
        String imageUrl,

        @NotNull(message = "Veg flag is required")
        Boolean isVeg,

        @NotNull(message = "Availability flag is required")
        Boolean isAvailable,

        @NotNull(message = "Spicy level is required")
        @Min(value = 0, message = "Spicy level must be between 0 and 5")
        @Max(value = 5, message = "Spicy level must be between 0 and 5")
        Integer spicyLevel,

        @NotNull(message = "Preparation time is required")
        @Min(value = 1, message = "Preparation time must be at least 1 minute")
        Integer preparationTime,

        @NotNull(message = "Category ID is required")
        Long categoryId
) {
}
