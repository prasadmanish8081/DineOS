package com.dineos.dto.response;

import com.dineos.enums.PaymentGatewayStatus;
import com.dineos.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentTransactionResponse(
        Long id,
        Long orderId,
        String razorpayOrderId,
        String razorpayPaymentId,
        PaymentGatewayStatus gatewayStatus,
        PaymentStatus paymentStatus,
        BigDecimal amount,
        String currency,
        String failureCode,
        String failureDescription,
        Instant createdAt,
        Instant updatedAt
) {
}
