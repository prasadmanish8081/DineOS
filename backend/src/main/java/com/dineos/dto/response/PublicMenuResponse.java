package com.dineos.dto.response;

import java.util.List;

public record PublicMenuResponse(
        RestaurantResponse restaurant,
        TableResponse table,
        List<CategoryMenuResponse> categories
) {
}
