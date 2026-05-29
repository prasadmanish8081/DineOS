package com.dineos.service;

import com.dineos.dto.request.KitchenUserCreateRequest;
import com.dineos.dto.response.UserResponse;

public interface UserService {

    UserResponse getCurrentUser(String email);

    UserResponse createKitchenUser(Long restaurantId, String ownerEmail, KitchenUserCreateRequest request);
}
