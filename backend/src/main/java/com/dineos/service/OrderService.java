package com.dineos.service;

import com.dineos.dto.request.PlaceOrderRequest;
import com.dineos.dto.request.UpdateOrderStatusRequest;
import com.dineos.dto.response.OrderPageResponse;
import com.dineos.dto.response.OrderResponse;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderResponse placeOrder(Long restaurantId, Long tableId, String customerEmail, PlaceOrderRequest request);

    OrderResponse placeOrder(String customerEmail, PlaceOrderRequest request);

    OrderResponse updateOrderStatus(Long restaurantId, Long orderId, String actorEmail, UpdateOrderStatusRequest request);

    OrderPageResponse<OrderResponse> getRestaurantOrders(Long restaurantId, String actorEmail, Pageable pageable);

    OrderPageResponse<OrderResponse> getLivePendingOrders(Long restaurantId, String actorEmail, Pageable pageable);
}
