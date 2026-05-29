package com.dineos.service.impl;

import com.dineos.dto.response.OrderRealtimeEventPayload;
import com.dineos.dto.response.OrderResponse;
import com.dineos.enums.OrderRealtimeEventType;
import com.dineos.service.RealtimeOrderBroadcastService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class RealtimeOrderBroadcastServiceImpl implements RealtimeOrderBroadcastService {

    private final SimpMessagingTemplate messagingTemplate;

    public RealtimeOrderBroadcastServiceImpl(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void broadcastNewOrder(OrderResponse order) {
        send(order, OrderRealtimeEventType.NEW_ORDER);
    }

    @Override
    public void broadcastOrderUpdated(OrderResponse order) {
        send(order, OrderRealtimeEventType.ORDER_UPDATED);
    }

    @Override
    public void broadcastOrderReady(OrderResponse order) {
        send(order, OrderRealtimeEventType.ORDER_READY);
    }

    private void send(OrderResponse order, OrderRealtimeEventType type) {
        Long restaurantId = order.restaurantId();
        if (restaurantId == null) {
            return;
        }

        OrderRealtimeEventPayload payload = new OrderRealtimeEventPayload(
                type,
                order,
                restaurantId,
                order.id(),
                order.status(),
                Instant.now()
        );

        messagingTemplate.convertAndSend("/topic/restaurants/" + restaurantId + "/orders", payload);
        messagingTemplate.convertAndSend("/topic/restaurants/" + restaurantId + "/kitchen", payload);
    }
}
