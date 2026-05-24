package com.dineos.payment;

import java.math.BigDecimal;

public record RazorpayOrderDetails(
        String id,
        BigDecimal amount,
        String currency,
        String receipt,
        String status
) {
}
