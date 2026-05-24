package com.dineos.service.impl;

import com.dineos.dto.request.PlaceOrderItemRequest;
import com.dineos.dto.request.PlaceOrderRequest;
import com.dineos.dto.request.UpdateOrderStatusRequest;
import com.dineos.dto.response.OrderPageResponse;
import com.dineos.dto.response.OrderResponse;
import com.dineos.entity.MenuItem;
import com.dineos.entity.Order;
import com.dineos.entity.OrderItem;
import com.dineos.entity.Restaurant;
import com.dineos.entity.RestaurantTable;
import com.dineos.entity.User;
import com.dineos.enums.OrderStatus;
import com.dineos.enums.PaymentStatus;
import com.dineos.enums.Role;
import com.dineos.exception.InvalidOrderStatusTransitionException;
import com.dineos.exception.MenuItemNotFoundException;
import com.dineos.exception.OrderForbiddenException;
import com.dineos.exception.OrderNotFoundException;
import com.dineos.exception.RestaurantForbiddenException;
import com.dineos.exception.RestaurantNotFoundException;
import com.dineos.exception.ResourceNotFoundException;
import com.dineos.exception.TableNotFoundException;
import com.dineos.event.OrderCreatedEvent;
import com.dineos.event.OrderStatusChangedEvent;
import com.dineos.repository.MenuItemRepository;
import com.dineos.repository.OrderRepository;
import com.dineos.repository.RestaurantRepository;
import com.dineos.repository.RestaurantTableRepository;
import com.dineos.repository.UserRepository;
import com.dineos.service.OrderService;
import org.springframework.context.ApplicationEventPublisher;
import com.dineos.util.OrderMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantTableRepository restaurantTableRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            MenuItemRepository menuItemRepository,
            RestaurantRepository restaurantRepository,
            RestaurantTableRepository restaurantTableRepository,
            UserRepository userRepository,
            ApplicationEventPublisher applicationEventPublisher
    ) {
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
        this.restaurantTableRepository = restaurantTableRepository;
        this.userRepository = userRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    @Transactional
    public OrderResponse placeOrder(Long restaurantId, Long tableId, String customerEmail, PlaceOrderRequest request) {
        Restaurant restaurant = findRestaurant(restaurantId);
        RestaurantTable table = findTable(tableId, restaurantId);
        User customer = customerEmail == null || customerEmail.isBlank() ? null : findUser(customerEmail);
        ensureCanPlaceOrder(customer);

        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setRestaurant(restaurant);
        order.setTable(table);
        order.setCustomer(customer);
        order.setStatus(OrderStatus.PLACED);
        order.setPaymentStatus(PaymentStatus.PENDING);

        List<OrderItem> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (PlaceOrderItemRequest itemRequest : request.items()) {
            MenuItem menuItem = menuItemRepository.findByIdAndRestaurant_Id(itemRequest.menuItemId(), restaurantId)
                    .orElseThrow(() -> new MenuItemNotFoundException("Menu item not found"));

            if (!Boolean.TRUE.equals(menuItem.getIsAvailable())) {
                throw new MenuItemNotFoundException("Menu item is currently unavailable");
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(itemRequest.quantity());
            orderItem.setPrice(menuItem.getPrice());
            orderItem.setSubtotal(menuItem.getPrice().multiply(BigDecimal.valueOf(itemRequest.quantity())));
            items.add(orderItem);

            totalAmount = totalAmount.add(orderItem.getSubtotal());
        }

        order.setItems(items);
        order.setTotalAmount(totalAmount);

        Order saved = saveOrderWithRetry(order);
        OrderResponse response = OrderMapper.toResponse(saved);
        applicationEventPublisher.publishEvent(new OrderCreatedEvent(response));
        return response;
    }

    private Order saveOrderWithRetry(Order order) {
        int attempts = 0;
        while (true) {
            attempts++;
            order.setOrderNumber(generateOrderNumber());
            try {
                return orderRepository.save(order);
            } catch (DataIntegrityViolationException ex) {
                if (attempts >= 5) {
                    throw ex;
                }
            }
        }
    }

    @Override
    @Transactional
    public OrderResponse placeOrder(String customerEmail, PlaceOrderRequest request) {
        if (request.tableId() == null) {
            throw new TableNotFoundException("Table ID is required");
        }

        RestaurantTable table = restaurantTableRepository.findById(request.tableId())
                .orElseThrow(() -> new TableNotFoundException("Table not found"));
        return placeOrder(table.getRestaurant().getId(), table.getId(), customerEmail, request);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long restaurantId, Long orderId, String actorEmail, UpdateOrderStatusRequest request) {
        Restaurant restaurant = findRestaurant(restaurantId);
        User actor = findUser(actorEmail);
        ensureCanManageRestaurant(actor, restaurant);

        Order order = orderRepository.findByIdAndRestaurant_Id(orderId, restaurantId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        validateTransition(order.getStatus(), request.status());
        order.setStatus(request.status());
        Order saved = orderRepository.save(order);
        OrderResponse response = OrderMapper.toResponse(saved);
        applicationEventPublisher.publishEvent(new OrderStatusChangedEvent(response));
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderPageResponse<OrderResponse> getRestaurantOrders(Long restaurantId, String actorEmail, Pageable pageable) {
        Restaurant restaurant = findRestaurant(restaurantId);
        ensureCanManageRestaurant(findUser(actorEmail), restaurant);
        Page<Order> page = orderRepository.findByRestaurant_Id(restaurantId, pageable);
        return toPagedResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderPageResponse<OrderResponse> getLivePendingOrders(Long restaurantId, String actorEmail, Pageable pageable) {
        Restaurant restaurant = findRestaurant(restaurantId);
        ensureCanManageRestaurant(findUser(actorEmail), restaurant);
        List<OrderStatus> activeStatuses = List.of(
                OrderStatus.PLACED,
                OrderStatus.ACCEPTED,
                OrderStatus.PREPARING,
                OrderStatus.READY,
                OrderStatus.SERVED
        );
        Page<Order> page = orderRepository.findByRestaurant_IdAndStatusIn(restaurantId, activeStatuses, pageable);
        return toPagedResponse(page);
    }

    private Restaurant findRestaurant(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found"));
    }

    private RestaurantTable findTable(Long tableId, Long restaurantId) {
        return restaurantTableRepository.findByIdAndRestaurant_Id(tableId, restaurantId)
                .orElseThrow(() -> new TableNotFoundException("Table not found"));
    }

    private User findUser(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void ensureCanPlaceOrder(User customer) {
        if (customer == null) {
            return;
        }
        if (customer.getRole() != Role.CUSTOMER && customer.getRole() != Role.ADMIN && customer.getRole() != Role.OWNER) {
            throw new OrderForbiddenException("You are not allowed to place this order");
        }
    }

    private void ensureCanManageRestaurant(User actor, Restaurant restaurant) {
        if (actor.getRole() == Role.ADMIN) {
            return;
        }
        if (actor.getRole() == Role.KITCHEN && actor.getAssignedRestaurant() != null
                && actor.getAssignedRestaurant().getId().equals(restaurant.getId())) {
            return;
        }
        if (actor.getRole() != Role.OWNER || restaurant.getOwner() == null ||
                !restaurant.getOwner().getEmail().equalsIgnoreCase(actor.getEmail())) {
            throw new RestaurantForbiddenException("You are not allowed to manage this restaurant");
        }
    }

    private void validateTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        if (currentStatus == newStatus) {
            return;
        }

        boolean valid = switch (currentStatus) {
            case PLACED -> newStatus == OrderStatus.ACCEPTED || newStatus == OrderStatus.PREPARING || newStatus == OrderStatus.CANCELLED;
            case ACCEPTED -> newStatus == OrderStatus.PREPARING || newStatus == OrderStatus.CANCELLED;
            case PREPARING -> newStatus == OrderStatus.READY || newStatus == OrderStatus.CANCELLED;
            case READY -> newStatus == OrderStatus.SERVED || newStatus == OrderStatus.CANCELLED;
            case SERVED -> newStatus == OrderStatus.COMPLETED;
            case COMPLETED, CANCELLED -> false;
        };

        if (!valid) {
            throw new InvalidOrderStatusTransitionException(
                    "Invalid status transition from " + currentStatus + " to " + newStatus
            );
        }
    }

    private OrderPageResponse<OrderResponse> toPagedResponse(Page<Order> page) {
        return new OrderPageResponse<>(
                page.getContent().stream().map(OrderMapper::toResponse).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext()
        );
    }

    private String generateOrderNumber() {
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        return "ORD-" + date + "-" + suffix;
    }
}
