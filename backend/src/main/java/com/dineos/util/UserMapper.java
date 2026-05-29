package com.dineos.util;

import com.dineos.dto.response.UserResponse;
import com.dineos.entity.User;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt(),
                user.getRestaurant() == null ? null : user.getRestaurant().getId(),
                user.getAssignedRestaurant() == null ? null : user.getAssignedRestaurant().getId()
        );
    }
}
