package com.dineos.controller;

import com.dineos.dto.response.PublicMenuResponse;
import com.dineos.service.PublicMenuService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/menu")
public class PublicMenuController {

    private final PublicMenuService publicMenuService;

    public PublicMenuController(PublicMenuService publicMenuService) {
        this.publicMenuService = publicMenuService;
    }

    @GetMapping("/{restaurantSlug}/table/{tableNumber}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<PublicMenuResponse> getMenuForQr(
            @PathVariable String restaurantSlug,
            @PathVariable String tableNumber
    ) {
        return ResponseEntity.ok(publicMenuService.getMenuForQr(restaurantSlug, tableNumber));
    }
}
