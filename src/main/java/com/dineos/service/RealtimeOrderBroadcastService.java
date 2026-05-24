package com.dineos.service;

import com.dineos.dto.response.OrderResponse;

public interface RealtimeOrderBroadcastService {

    void broadcastNewOrder(OrderResponse order);

    void broadcastOrderUpdated(OrderResponse order);

    void broadcastOrderReady(OrderResponse order);
}
