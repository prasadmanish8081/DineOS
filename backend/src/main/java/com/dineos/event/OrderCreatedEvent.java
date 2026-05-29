package com.dineos.event;

import com.dineos.dto.response.OrderResponse;

public record OrderCreatedEvent(OrderResponse order) {
}
