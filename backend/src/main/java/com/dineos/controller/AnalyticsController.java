package com.dineos.controller;

import com.dineos.dto.response.AnalyticsDashboardResponse;
import com.dineos.security.UserPrincipal;
import com.dineos.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<AnalyticsDashboardResponse> getDashboard(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long restaurantId
    ) {
        return ResponseEntity.ok(analyticsService.getRestaurantAnalytics(restaurantId, principal.getUsername()));
    }
}
