package com.dineos.dto.response;

import com.dineos.enums.OrderRealtimeEventType;
import com.dineos.enums.OrderStatus;

import java.time.Instant;

public record OrderRealtimeEventPayload(
        OrderRealtimeEventType type,
        OrderResponse order,
        Long restaurantId,
        Long orderId,
        OrderStatus status,
        Instant emittedAt
) {
}
