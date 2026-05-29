package com.dineos.service;

import com.dineos.dto.request.CategoryCreateRequest;
import com.dineos.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(Long restaurantId, String actorEmail, CategoryCreateRequest request);

    List<CategoryResponse> getCategoriesByRestaurant(Long restaurantId);
}
