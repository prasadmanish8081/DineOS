package com.dineos.controller;

import com.dineos.dto.request.PlaceOrderRequest;
import com.dineos.dto.request.UpdateOrderStatusRequest;
import com.dineos.dto.response.OrderPageResponse;
import com.dineos.dto.response.OrderResponse;
import com.dineos.security.UserPrincipal;
import com.dineos.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/restaurants/{restaurantId}/tables/{tableId}/orders")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OWNER', 'ADMIN')")
    public ResponseEntity<OrderResponse> placeOrder(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long restaurantId,
            @PathVariable Long tableId,
            @Valid @RequestBody PlaceOrderRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.placeOrder(restaurantId, tableId, principal.getUsername(), request));
    }

    @PostMapping("/orders")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OWNER', 'ADMIN')")
    public ResponseEntity<OrderResponse> placeOrder(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody PlaceOrderRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.placeOrder(principal.getUsername(), request));
    }

    @PostMapping("/public/orders")
    @PreAuthorize("permitAll()")
    public ResponseEntity<OrderResponse> placePublicOrder(@Valid @RequestBody PlaceOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.placeOrder(null, request));
    }

    @PatchMapping("/restaurants/{restaurantId}/orders/{orderId}/status")
    @PreAuthorize("hasAnyRole('KITCHEN', 'OWNER', 'ADMIN')")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long restaurantId,
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request
    ) {
        return ResponseEntity.ok(orderService.updateOrderStatus(restaurantId, orderId, principal.getUsername(), request));
    }

    @GetMapping("/restaurants/{restaurantId}/orders")
    @PreAuthorize("hasAnyRole('KITCHEN', 'OWNER', 'ADMIN')")
    public ResponseEntity<OrderPageResponse<OrderResponse>> getRestaurantOrders(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long restaurantId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(orderService.getRestaurantOrders(restaurantId, principal.getUsername(), pageable));
    }

    @GetMapping("/restaurants/{restaurantId}/orders/pending")
    @PreAuthorize("hasAnyRole('KITCHEN', 'OWNER', 'ADMIN')")
    public ResponseEntity<OrderPageResponse<OrderResponse>> getLivePendingOrders(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long restaurantId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(orderService.getLivePendingOrders(restaurantId, principal.getUsername(), pageable));
    }
}
