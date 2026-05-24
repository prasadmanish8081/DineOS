package com.dineos.service;

import com.dineos.dto.request.MenuItemCreateRequest;
import com.dineos.dto.request.MenuItemUpdateRequest;
import com.dineos.dto.response.MenuItemResponse;
import com.dineos.dto.response.MenuPageResponse;
import org.springframework.data.domain.Pageable;

public interface MenuItemService {

    MenuItemResponse addMenuItem(Long restaurantId, String actorEmail, MenuItemCreateRequest request);

    MenuItemResponse updateMenuItem(Long restaurantId, Long menuItemId, String actorEmail, MenuItemUpdateRequest request);

    void deleteMenuItem(Long restaurantId, Long menuItemId, String actorEmail);

    MenuPageResponse<MenuItemResponse> getMenuByRestaurant(Long restaurantId, Boolean veg, Pageable pageable);
}
