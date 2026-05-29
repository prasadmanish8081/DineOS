package com.dineos.controller;

import com.dineos.dto.request.RestaurantCreateRequest;
import com.dineos.dto.request.RestaurantUpdateRequest;
import com.dineos.dto.response.RestaurantResponse;
import com.dineos.security.UserPrincipal;
import com.dineos.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<RestaurantResponse> createRestaurant(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody RestaurantCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(restaurantService.createRestaurant(principal.getUsername(), request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<RestaurantResponse> updateRestaurant(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody RestaurantUpdateRequest request
    ) {
        return ResponseEntity.ok(restaurantService.updateRestaurant(id, principal.getUsername(), request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<RestaurantResponse> getRestaurantById(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(restaurantService.getRestaurantById(id, principal.getUsername()));
    }

    @GetMapping("/slug/{slug}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<RestaurantResponse> getRestaurantBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(restaurantService.getRestaurantBySlug(slug));
    }
}
