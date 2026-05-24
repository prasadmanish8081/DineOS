package com.dineos.payment;

import java.math.BigDecimal;
import java.util.Map;

public interface RazorpayGatewayClient {

    RazorpayOrderDetails createOrder(BigDecimal amountInRupees, String currency, String receipt, Map<String, String> notes);

    boolean verifyPaymentSignature(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature);

    boolean verifyWebhookSignature(String rawBody, String webhookSignature);
}
