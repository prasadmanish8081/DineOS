package com.dineos.controller;

import com.dineos.dto.request.KitchenUserCreateRequest;
import com.dineos.dto.response.UserResponse;
import com.dineos.security.UserPrincipal;
import com.dineos.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/kitchen-users")
public class RestaurantStaffController {

    private final UserService userService;

    public RestaurantStaffController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<UserResponse> createKitchenUser(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long restaurantId,
            @Valid @RequestBody KitchenUserCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createKitchenUser(restaurantId, principal.getUsername(), request));
    }
}
