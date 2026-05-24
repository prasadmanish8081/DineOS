package com.dineos.payment;

import com.dineos.config.RazorpayProperties;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Component
public class RazorpayGatewayClientImpl implements RazorpayGatewayClient {

    private final RazorpayProperties razorpayProperties;

    public RazorpayGatewayClientImpl(RazorpayProperties razorpayProperties) {
        this.razorpayProperties = razorpayProperties;
    }

    @Override
    public RazorpayOrderDetails createOrder(BigDecimal amountInRupees, String currency, String receipt, Map<String, String> notes) {
        try {
            JSONObject orderRequest = new JSONObject();
            long amountInPaise = amountInRupees
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(0, RoundingMode.HALF_UP)
                    .longValueExact();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", currency);
            orderRequest.put("receipt", receipt);
            if (notes != null && !notes.isEmpty()) {
                orderRequest.put("notes", new JSONObject(notes));
            }

            Order order = client().orders.create(orderRequest);
            return new RazorpayOrderDetails(
                    String.valueOf(order.get("id")),
                    new BigDecimal(String.valueOf(order.get("amount"))).movePointLeft(2),
                    String.valueOf(order.get("currency")),
                    String.valueOf(order.get("receipt")),
                    String.valueOf(order.get("status"))
            );
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to create Razorpay order", ex);
        }
    }

    @Override
    public boolean verifyPaymentSignature(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", razorpayOrderId);
            options.put("razorpay_payment_id", razorpayPaymentId);
            options.put("razorpay_signature", razorpaySignature);
            return Utils.verifyPaymentSignature(options, razorpayProperties.getKeySecret());
        } catch (RazorpayException ex) {
            throw new IllegalStateException("Failed to verify Razorpay payment signature", ex);
        }
    }

    @Override
    public boolean verifyWebhookSignature(String rawBody, String webhookSignature) {
        try {
            return Utils.verifyWebhookSignature(rawBody, webhookSignature, razorpayProperties.getWebhookSecret());
        } catch (RazorpayException ex) {
            throw new IllegalStateException("Failed to verify Razorpay webhook signature", ex);
        }
    }

    private RazorpayClient client() {
        try {
            return new RazorpayClient(razorpayProperties.getKeyId(), razorpayProperties.getKeySecret());
        } catch (RazorpayException ex) {
            throw new IllegalStateException("Failed to create Razorpay client", ex);
        }
    }
}
