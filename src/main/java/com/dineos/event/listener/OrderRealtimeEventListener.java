package com.dineos.event.listener;

import com.dineos.dto.response.OrderResponse;
import com.dineos.enums.OrderStatus;
import com.dineos.event.OrderCreatedEvent;
import com.dineos.event.OrderStatusChangedEvent;
import com.dineos.service.RealtimeOrderBroadcastService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrderRealtimeEventListener {

    private final RealtimeOrderBroadcastService broadcastService;

    public OrderRealtimeEventListener(RealtimeOrderBroadcastService broadcastService) {
        this.broadcastService = broadcastService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderCreated(OrderCreatedEvent event) {
        broadcastService.broadcastNewOrder(event.order());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderStatusChanged(OrderStatusChangedEvent event) {
        OrderResponse order = event.order();
        broadcastService.broadcastOrderUpdated(order);
        if (order.status() == OrderStatus.READY) {
            broadcastService.broadcastOrderReady(order);
        }
    }
}
