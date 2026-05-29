package com.dineos.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record PlaceOrderRequest(
        Long tableId,

        @NotEmpty(message = "At least one item is required")
        List<@Valid PlaceOrderItemRequest> items
) {
}
