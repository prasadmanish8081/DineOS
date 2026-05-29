package com.dineos.dto.response;

public record TopSellingItemResponse(
        Long menuItemId,
        String menuItemName,
        Long quantitySold,
        Long orderCount
) {
}
