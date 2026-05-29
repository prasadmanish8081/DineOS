package com.dineos.util;

import com.dineos.dto.response.OrderItemResponse;
import com.dineos.dto.response.OrderResponse;
import com.dineos.entity.Order;
import com.dineos.entity.OrderItem;

import java.util.List;

public final class OrderMapper {

    private OrderMapper() {
    }

    public static OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getRestaurant() == null ? null : order.getRestaurant().getId(),
                order.getRestaurant() == null ? null : order.getRestaurant().getSlug(),
                order.getTable() == null ? null : order.getTable().getId(),
                order.getTable() == null ? null : order.getTable().getTableNumber(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getPaymentStatus(),
                order.getCreatedAt(),
                order.getItems() == null ? List.of() : order.getItems().stream().map(OrderMapper::toItemResponse).toList()
        );
    }

    public static OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getMenuItem() == null ? null : item.getMenuItem().getId(),
                item.getMenuItem() == null ? null : item.getMenuItem().getName(),
                item.getQuantity(),
                item.getPrice(),
                item.getSubtotal()
        );
    }
}
