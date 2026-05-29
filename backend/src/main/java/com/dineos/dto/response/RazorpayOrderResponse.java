package com.dineos.dto.response;

import java.math.BigDecimal;

public record RazorpayOrderResponse(
        Long orderId,
        String orderNumber,
        String razorpayOrderId,
        BigDecimal amount,
        String currency,
        String keyId,
        String status
) {
}
