package com.dineos.dto.response;

import com.dineos.enums.OrderStatus;
import com.dineos.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderNumber,
        Long restaurantId,
        String restaurantSlug,
        Long tableId,
        String tableNumber,
        OrderStatus status,
        BigDecimal totalAmount,
        PaymentStatus paymentStatus,
        Instant createdAt,
        List<OrderItemResponse> items
) {
}
