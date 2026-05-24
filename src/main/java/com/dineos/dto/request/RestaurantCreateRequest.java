package com.dineos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RestaurantCreateRequest(
        @NotBlank(message = "Restaurant name is required")
        @Size(min = 2, max = 150, message = "Restaurant name must be between 2 and 150 characters")
        String name,

        @NotBlank(message = "Address is required")
        @Size(min = 5, max = 255, message = "Address must be between 5 and 255 characters")
        String address,

        @NotBlank(message = "Phone is required")
        @Size(min = 7, max = 30, message = "Phone must be between 7 and 30 characters")
        String phone,

        @NotBlank(message = "GST number is required")
        @Size(min = 3, max = 50, message = "GST number must be between 3 and 50 characters")
        String gstNumber,

        @Size(max = 500, message = "Logo URL must not exceed 500 characters")
        String logoUrl
) {
}
