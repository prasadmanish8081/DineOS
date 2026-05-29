package com.dineos.dto.response;

public record TableResponse(
        Long id,
        String tableNumber,
        String qrCodeUrl,
        Long restaurantId,
        String restaurantSlug
) {
}
