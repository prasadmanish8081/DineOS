package com.dineos.service;

import com.dineos.dto.request.FailedRazorpayPaymentRequest;
import com.dineos.dto.request.VerifyRazorpayPaymentRequest;
import com.dineos.dto.response.PaymentTransactionResponse;
import com.dineos.dto.response.RazorpayOrderResponse;

public interface PaymentService {

    RazorpayOrderResponse createRazorpayOrder(Long restaurantId, Long orderId, String actorEmail);

    PaymentTransactionResponse verifyPayment(Long restaurantId, Long orderId, String actorEmail, VerifyRazorpayPaymentRequest request);

    PaymentTransactionResponse registerFailedPayment(Long restaurantId, Long orderId, String actorEmail, FailedRazorpayPaymentRequest request);

    void handleWebhook(String rawBody, String webhookSignature, String eventId);
}
