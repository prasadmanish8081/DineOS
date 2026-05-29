package com.dineos.util;

import com.dineos.dto.response.RestaurantResponse;
import com.dineos.entity.Restaurant;

public final class RestaurantMapper {

    private RestaurantMapper() {
    }

    public static RestaurantResponse toResponse(Restaurant restaurant) {
        return new RestaurantResponse(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getSlug(),
                restaurant.getAddress(),
                restaurant.getPhone(),
                restaurant.getGstNumber(),
                restaurant.getLogoUrl(),
                restaurant.getCreatedAt(),
                restaurant.getOwner() == null ? null : restaurant.getOwner().getId()
        );
    }
}
