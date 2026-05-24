package com.dineos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record TableCreateRequest(
        @NotBlank(message = "Table number is required")
        @Size(min = 1, max = 50, message = "Table number must be between 1 and 50 characters")
        @Pattern(
                regexp = "^[A-Za-z0-9_-]+$",
                message = "Table number can contain only letters, numbers, underscores, and hyphens"
        )
        String tableNumber
) {
}
