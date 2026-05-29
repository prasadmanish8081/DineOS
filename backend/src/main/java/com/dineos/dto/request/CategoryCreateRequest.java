package com.dineos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryCreateRequest(
        @NotBlank(message = "Category name is required")
        @Size(min = 2, max = 120, message = "Category name must be between 2 and 120 characters")
        String name
) {
}
