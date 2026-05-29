package com.dineos.util;

import com.dineos.dto.response.TableResponse;
import com.dineos.entity.RestaurantTable;

public final class TableMapper {

    private TableMapper() {
    }

    public static TableResponse toResponse(RestaurantTable table) {
        return new TableResponse(
                table.getId(),
                table.getTableNumber(),
                table.getRestaurant() == null ? null : "/#/menu/" + table.getRestaurant().getSlug() + "/table/" + table.getTableNumber(),
                table.getRestaurant() == null ? null : table.getRestaurant().getId(),
                table.getRestaurant() == null ? null : table.getRestaurant().getSlug()
        );
    }
}
