package com.dineos.service.impl;

import com.dineos.config.RazorpayProperties;
import com.dineos.dto.request.FailedRazorpayPaymentRequest;
import com.dineos.dto.request.VerifyRazorpayPaymentRequest;
import com.dineos.dto.response.PaymentTransactionResponse;
import com.dineos.dto.response.RazorpayOrderResponse;
import com.dineos.entity.Order;
import com.dineos.entity.PaymentTransaction;
import com.dineos.entity.PaymentWebhookEvent;
import com.dineos.entity.Restaurant;
import com.dineos.entity.User;
import com.dineos.enums.OrderStatus;
import com.dineos.enums.PaymentGatewayStatus;
import com.dineos.enums.PaymentStatus;
import com.dineos.enums.Role;
import com.dineos.exception.OrderForbiddenException;
import com.dineos.exception.OrderNotFoundException;
import com.dineos.exception.PaymentAlreadyVerifiedException;
import com.dineos.exception.PaymentNotFoundException;
import com.dineos.exception.PaymentVerificationException;
import com.dineos.exception.RestaurantForbiddenException;
import com.dineos.exception.ResourceNotFoundException;
import com.dineos.event.OrderStatusChangedEvent;
import com.dineos.payment.RazorpayGatewayClient;
import com.dineos.payment.RazorpayOrderDetails;
import com.dineos.repository.OrderRepository;
import com.dineos.repository.PaymentTransactionRepository;
import com.dineos.repository.PaymentWebhookEventRepository;
import com.dineos.repository.RestaurantRepository;
import com.dineos.repository.RestaurantTableRepository;
import com.dineos.repository.UserRepository;
import com.dineos.service.PaymentService;
import com.dineos.util.PaymentTransactionMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentWebhookEventRepository paymentWebhookEventRepository;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantTableRepository restaurantTableRepository;
    private final UserRepository userRepository;
    private final RazorpayGatewayClient razorpayGatewayClient;
    private final RazorpayProperties razorpayProperties;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    public PaymentServiceImpl(
            OrderRepository orderRepository,
            PaymentTransactionRepository paymentTransactionRepository,
            PaymentWebhookEventRepository paymentWebhookEventRepository,
            RestaurantRepository restaurantRepository,
            RestaurantTableRepository restaurantTableRepository,
            UserRepository userRepository,
            RazorpayGatewayClient razorpayGatewayClient,
            RazorpayProperties razorpayProperties,
            ObjectMapper objectMapper,
            ApplicationEventPublisher applicationEventPublisher
    ) {
        this.orderRepository = orderRepository;
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.paymentWebhookEventRepository = paymentWebhookEventRepository;
        this.restaurantRepository = restaurantRepository;
        this.restaurantTableRepository = restaurantTableRepository;
        this.userRepository = userRepository;
        this.razorpayGatewayClient = razorpayGatewayClient;
        this.razorpayProperties = razorpayProperties;
        this.objectMapper = objectMapper;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    @Transactional
    public RazorpayOrderResponse createRazorpayOrder(Long restaurantId, Long orderId, String actorEmail) {
        Order order = findOrder(restaurantId, orderId);
        ensureOrderAccess(actorEmail, order);

        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            throw new PaymentAlreadyVerifiedException("Order is already paid");
        }

        PaymentTransaction existingTransaction = paymentTransactionRepository.findFirstByOrder_IdOrderByCreatedAtDesc(order.getId()).orElse(null);
        if (existingTransaction != null
                && existingTransaction.getPaymentStatus() != PaymentStatus.FAILED
                && existingTransaction.getRazorpayOrderId() != null) {
            return new RazorpayOrderResponse(
                    order.getId(),
                    order.getOrderNumber(),
                    existingTransaction.getRazorpayOrderId(),
                    order.getTotalAmount(),
                    existingTransaction.getCurrency(),
                    razorpayProperties.getKeyId(),
                    "created"
            );
        }

        RazorpayOrderDetails gatewayOrder = razorpayGatewayClient.createOrder(
                order.getTotalAmount(),
                razorpayProperties.getCurrency(),
                buildReceipt(order),
                buildNotes(order)
        );

        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setOrder(order);
        transaction.setRazorpayOrderId(gatewayOrder.id());
        transaction.setAmount(order.getTotalAmount());
        transaction.setCurrency(gatewayOrder.currency() == null ? razorpayProperties.getCurrency() : gatewayOrder.currency());
        transaction.setGatewayStatus(PaymentGatewayStatus.CREATED);
        transaction.setPaymentStatus(PaymentStatus.PENDING);
        paymentTransactionRepository.save(transaction);

        return new RazorpayOrderResponse(
                order.getId(),
                order.getOrderNumber(),
                gatewayOrder.id(),
                order.getTotalAmount(),
                transaction.getCurrency(),
                razorpayProperties.getKeyId(),
                gatewayOrder.status()
        );
    }

    @Override
    @Transactional
    public PaymentTransactionResponse verifyPayment(Long restaurantId, Long orderId, String actorEmail, VerifyRazorpayPaymentRequest request) {
        Order order = findOrder(restaurantId, orderId);
        ensureOrderAccess(actorEmail, order);

        PaymentTransaction transaction = paymentTransactionRepository.findByRazorpayOrderId(request.razorpayOrderId())
                .orElseThrow(() -> new PaymentNotFoundException("Payment transaction not found"));

        if (transaction.getPaymentStatus() == PaymentStatus.PAID) {
            throw new PaymentAlreadyVerifiedException("Payment is already verified");
        }

        if (!transaction.getOrder().getId().equals(order.getId())) {
            throw new PaymentVerificationException("Payment does not belong to this order");
        }

        if (!razorpayGatewayClient.verifyPaymentSignature(
                request.razorpayOrderId(),
                request.razorpayPaymentId(),
                request.razorpaySignature()
        )) {
            markFailed(transaction, "SIGNATURE_MISMATCH", "Invalid payment signature");
            throw new PaymentVerificationException("Invalid payment signature");
        }

        transaction.setRazorpayPaymentId(request.razorpayPaymentId());
        transaction.setRazorpaySignature(request.razorpaySignature());
        transaction.setGatewayStatus(PaymentGatewayStatus.VERIFIED);
        transaction.setPaymentStatus(PaymentStatus.PAID);
        transaction.setFailureCode(null);
        transaction.setFailureDescription(null);

        order.setPaymentStatus(PaymentStatus.PAID);
        order.setStatus(order.getStatus() == OrderStatus.PLACED ? OrderStatus.ACCEPTED : order.getStatus());

        PaymentTransaction saved = paymentTransactionRepository.save(transaction);
        orderRepository.save(order);
        applicationEventPublisher.publishEvent(new OrderStatusChangedEvent(com.dineos.util.OrderMapper.toResponse(order)));
        return PaymentTransactionMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public PaymentTransactionResponse registerFailedPayment(Long restaurantId, Long orderId, String actorEmail, FailedRazorpayPaymentRequest request) {
        Order order = findOrder(restaurantId, orderId);
        ensureOrderAccess(actorEmail, order);

        PaymentTransaction transaction = paymentTransactionRepository.findByRazorpayOrderId(request.razorpayOrderId())
                .orElseThrow(() -> new PaymentNotFoundException("Payment transaction not found"));

        transaction.setRazorpayPaymentId(request.razorpayPaymentId());
        transaction.setGatewayStatus(PaymentGatewayStatus.FAILED);
        transaction.setPaymentStatus(PaymentStatus.FAILED);
        transaction.setFailureCode(request.errorCode());
        transaction.setFailureDescription(request.errorDescription());
        order.setPaymentStatus(PaymentStatus.FAILED);

        PaymentTransaction saved = paymentTransactionRepository.save(transaction);
        orderRepository.save(order);
        applicationEventPublisher.publishEvent(new OrderStatusChangedEvent(com.dineos.util.OrderMapper.toResponse(order)));
        return PaymentTransactionMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void handleWebhook(String rawBody, String webhookSignature, String eventId) {
        if (webhookSignature == null || webhookSignature.isBlank()) {
            throw new PaymentVerificationException("Missing webhook signature");
        }

        if (!razorpayGatewayClient.verifyWebhookSignature(rawBody, webhookSignature)) {
            throw new PaymentVerificationException("Invalid webhook signature");
        }

        if (eventId != null && !eventId.isBlank() && paymentWebhookEventRepository.existsByEventId(eventId)) {
            return;
        }

        JsonNode root;
        try {
            root = objectMapper.readTree(rawBody);
        } catch (Exception ex) {
            throw new PaymentVerificationException("Invalid webhook payload");
        }

        String event = text(root, "event");
        JsonNode payload = root.path("payload");
        JsonNode paymentEntity = payload.path("payment").path("entity");
        String razorpayOrderId = text(paymentEntity, "order_id");
        String razorpayPaymentId = text(paymentEntity, "id");
        String failureCode = text(paymentEntity.path("error"), "code");
        String failureDescription = text(paymentEntity.path("error"), "description");

        if (razorpayOrderId == null || razorpayOrderId.isBlank()) {
            throw new PaymentVerificationException("Webhook payload missing order id");
        }

        PaymentTransaction transaction = paymentTransactionRepository.findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment transaction not found"));

        Order order = transaction.getOrder();

        if (eventId != null && !eventId.isBlank()) {
            PaymentWebhookEvent processed = new PaymentWebhookEvent();
            processed.setEventId(eventId);
            processed.setEventType(event);
            processed.setRawPayload(rawBody);
            paymentWebhookEventRepository.save(processed);
        }

        transaction.setWebhookEventId(eventId);
        transaction.setWebhookEventType(event);
        transaction.setRawWebhookPayload(rawBody);

        if ("payment.failed".equals(event)) {
            if (order.getPaymentStatus() == PaymentStatus.PAID) {
                return;
            }
            transaction.setRazorpayPaymentId(razorpayPaymentId);
            transaction.setGatewayStatus(PaymentGatewayStatus.FAILED);
            transaction.setPaymentStatus(PaymentStatus.FAILED);
            transaction.setFailureCode(failureCode);
            transaction.setFailureDescription(failureDescription);
            order.setPaymentStatus(PaymentStatus.FAILED);
        } else if ("payment.authorized".equals(event) || "payment.captured".equals(event) || "order.paid".equals(event)) {
            transaction.setRazorpayPaymentId(razorpayPaymentId);
            transaction.setGatewayStatus("payment.captured".equals(event) ? PaymentGatewayStatus.CAPTURED : PaymentGatewayStatus.AUTHORIZED);
            transaction.setPaymentStatus(PaymentStatus.PAID);
            transaction.setFailureCode(null);
            transaction.setFailureDescription(null);
            order.setPaymentStatus(PaymentStatus.PAID);
        } else {
            return;
        }

        paymentTransactionRepository.save(transaction);
        orderRepository.save(order);
        applicationEventPublisher.publishEvent(new OrderStatusChangedEvent(com.dineos.util.OrderMapper.toResponse(order)));
    }

    private Order findOrder(Long restaurantId, Long orderId) {
        return orderRepository.findByIdAndRestaurant_Id(orderId, restaurantId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
    }

    private User findUser(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void ensureOrderAccess(String actorEmail, Order order) {
        if (actorEmail == null || actorEmail.isBlank()) {
            return;
        }
        ensureOrderAccess(findUser(actorEmail), order);
    }

    private void ensureOrderAccess(User actor, Order order) {
        if (actor.getRole() == Role.ADMIN || actor.getRole() == Role.OWNER || actor.getRole() == Role.KITCHEN) {
            if (actor.getRole() == Role.OWNER && order.getRestaurant().getOwner() != null
                    && !order.getRestaurant().getOwner().getEmail().equalsIgnoreCase(actor.getEmail())) {
                throw new RestaurantForbiddenException("You are not allowed to access this order");
            }
            return;
        }
        if (actor.getRole() == Role.CUSTOMER && order.getCustomer() != null
                && order.getCustomer().getEmail().equalsIgnoreCase(actor.getEmail())) {
            return;
        }
        throw new OrderForbiddenException("You are not allowed to access this order");
    }

    private String buildReceipt(Order order) {
        return "order-" + order.getOrderNumber();
    }

    private Map<String, String> buildNotes(Order order) {
        Map<String, String> notes = new LinkedHashMap<>();
        notes.put("orderId", String.valueOf(order.getId()));
        notes.put("restaurantId", String.valueOf(order.getRestaurant().getId()));
        notes.put("orderNumber", order.getOrderNumber());
        notes.put("source", "dineos");
        return notes;
    }

    private void markFailed(PaymentTransaction transaction, String code, String description) {
        transaction.setGatewayStatus(PaymentGatewayStatus.FAILED);
        transaction.setPaymentStatus(PaymentStatus.FAILED);
        transaction.setFailureCode(code);
        transaction.setFailureDescription(description);
        Order order = transaction.getOrder();
        order.setPaymentStatus(PaymentStatus.FAILED);
        paymentTransactionRepository.save(transaction);
        orderRepository.save(order);
    }

    private String text(JsonNode node, String field) {
        if (node == null || node.isMissingNode()) {
            return null;
        }
        JsonNode child = node.get(field);
        return child == null || child.isNull() ? null : child.asText();
    }
}
