package com.dineos.service;

import com.dineos.dto.request.RestaurantCreateRequest;
import com.dineos.dto.request.RestaurantUpdateRequest;
import com.dineos.dto.response.RestaurantResponse;

public interface RestaurantService {

    RestaurantResponse createRestaurant(String ownerEmail, RestaurantCreateRequest request);

    RestaurantResponse updateRestaurant(Long restaurantId, String ownerEmail, RestaurantUpdateRequest request);

    RestaurantResponse getRestaurantById(Long restaurantId, String actorEmail);

    RestaurantResponse getRestaurantBySlug(String slug);
}
