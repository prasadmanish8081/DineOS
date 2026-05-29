package com.dineos.service;

import com.dineos.dto.response.AnalyticsDashboardResponse;

public interface AnalyticsService {

    AnalyticsDashboardResponse getRestaurantAnalytics(Long restaurantId, String actorEmail);
}
