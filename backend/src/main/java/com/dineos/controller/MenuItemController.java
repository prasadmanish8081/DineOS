package com.dineos.controller;

import com.dineos.dto.request.MenuItemCreateRequest;
import com.dineos.dto.request.MenuItemUpdateRequest;
import com.dineos.dto.response.MenuItemResponse;
import com.dineos.dto.response.MenuPageResponse;
import com.dineos.security.UserPrincipal;
import com.dineos.service.MenuItemService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/menu-items")
public class MenuItemController {

    private final MenuItemService menuItemService;

    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<MenuItemResponse> addMenuItem(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long restaurantId,
            @Valid @RequestBody MenuItemCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(menuItemService.addMenuItem(restaurantId, principal.getUsername(), request));
    }

    @PutMapping("/{menuItemId}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<MenuItemResponse> updateMenuItem(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long restaurantId,
            @PathVariable Long menuItemId,
            @Valid @RequestBody MenuItemUpdateRequest request
    ) {
        return ResponseEntity.ok(menuItemService.updateMenuItem(restaurantId, menuItemId, principal.getUsername(), request));
    }

    @DeleteMapping("/{menuItemId}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<Void> deleteMenuItem(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long restaurantId,
            @PathVariable Long menuItemId
    ) {
        menuItemService.deleteMenuItem(restaurantId, menuItemId, principal.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<MenuPageResponse<MenuItemResponse>> getMenuByRestaurant(
            @PathVariable Long restaurantId,
            @RequestParam(required = false) Boolean veg,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(menuItemService.getMenuByRestaurant(restaurantId, veg, pageable));
    }
}
