package com.dineos.controller;

import com.dineos.dto.request.FailedRazorpayPaymentRequest;
import com.dineos.dto.request.VerifyRazorpayPaymentRequest;
import com.dineos.dto.response.PaymentTransactionResponse;
import com.dineos.dto.response.RazorpayOrderResponse;
import com.dineos.security.UserPrincipal;
import com.dineos.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/restaurants/{restaurantId}/orders/{orderId}/payments/razorpay/create-order")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OWNER', 'ADMIN')")
    public ResponseEntity<RazorpayOrderResponse> createRazorpayOrder(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long restaurantId,
            @PathVariable Long orderId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.createRazorpayOrder(restaurantId, orderId, principal.getUsername()));
    }

    @PostMapping("/public/restaurants/{restaurantId}/orders/{orderId}/payments/razorpay/create-order")
    @PreAuthorize("permitAll()")
    public ResponseEntity<RazorpayOrderResponse> createPublicRazorpayOrder(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.createRazorpayOrder(restaurantId, orderId, null));
    }

    @PostMapping("/restaurants/{restaurantId}/orders/{orderId}/payments/razorpay/verify")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OWNER', 'ADMIN')")
    public ResponseEntity<PaymentTransactionResponse> verifyPayment(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long restaurantId,
            @PathVariable Long orderId,
            @Valid @RequestBody VerifyRazorpayPaymentRequest request
    ) {
        return ResponseEntity.ok(paymentService.verifyPayment(restaurantId, orderId, principal.getUsername(), request));
    }

    @PostMapping("/public/restaurants/{restaurantId}/orders/{orderId}/payments/razorpay/verify")
    @PreAuthorize("permitAll()")
    public ResponseEntity<PaymentTransactionResponse> verifyPublicPayment(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId,
            @Valid @RequestBody VerifyRazorpayPaymentRequest request
    ) {
        return ResponseEntity.ok(paymentService.verifyPayment(restaurantId, orderId, null, request));
    }

    @PostMapping("/restaurants/{restaurantId}/orders/{orderId}/payments/razorpay/failed")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OWNER', 'ADMIN')")
    public ResponseEntity<PaymentTransactionResponse> failedPayment(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long restaurantId,
            @PathVariable Long orderId,
            @Valid @RequestBody FailedRazorpayPaymentRequest request
    ) {
        return ResponseEntity.ok(paymentService.registerFailedPayment(restaurantId, orderId, principal.getUsername(), request));
    }

    @PostMapping("/public/restaurants/{restaurantId}/orders/{orderId}/payments/razorpay/failed")
    @PreAuthorize("permitAll()")
    public ResponseEntity<PaymentTransactionResponse> failedPublicPayment(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId,
            @Valid @RequestBody FailedRazorpayPaymentRequest request
    ) {
        return ResponseEntity.ok(paymentService.registerFailedPayment(restaurantId, orderId, null, request));
    }

    @PostMapping("/payments/webhooks/razorpay")
    public ResponseEntity<Void> razorpayWebhook(
            @RequestBody String rawBody,
            @RequestHeader(value = "X-Razorpay-Signature", required = false) String webhookSignature,
            @RequestHeader(value = "x-razorpay-event-id", required = false) String eventId
    ) {
        paymentService.handleWebhook(rawBody, webhookSignature, eventId);
        return ResponseEntity.ok().build();
    }
}
