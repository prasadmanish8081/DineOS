package com.dineos.util;

import com.dineos.dto.response.MenuItemResponse;
import com.dineos.entity.MenuItem;

public final class MenuItemMapper {

    private MenuItemMapper() {
    }

    public static MenuItemResponse toResponse(MenuItem item) {
        return new MenuItemResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.getImageUrl(),
                item.getIsVeg(),
                item.getIsAvailable(),
                item.getSpicyLevel(),
                item.getPreparationTime(),
                item.getCategory() == null ? null : item.getCategory().getId(),
                item.getCategory() == null ? null : item.getCategory().getName(),
                item.getRestaurant() == null ? null : item.getRestaurant().getId(),
                item.getCreatedAt()
        );
    }
}
