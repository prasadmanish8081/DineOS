package com.dineos.service;

import com.dineos.dto.response.PublicMenuResponse;

public interface PublicMenuService {

    PublicMenuResponse getMenuForQr(String restaurantSlug, String tableNumber);
}
