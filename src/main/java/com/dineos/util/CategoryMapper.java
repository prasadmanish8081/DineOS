package com.dineos.util;

import com.dineos.dto.response.CategoryResponse;
import com.dineos.entity.Category;

public final class CategoryMapper {

    private CategoryMapper() {
    }

    public static CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getRestaurant() == null ? null : category.getRestaurant().getId(),
                category.getCreatedAt()
        );
    }
}
