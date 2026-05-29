package com.dineos.util;

import com.dineos.dto.response.PaymentTransactionResponse;
import com.dineos.entity.PaymentTransaction;

public final class PaymentTransactionMapper {

    private PaymentTransactionMapper() {
    }

    public static PaymentTransactionResponse toResponse(PaymentTransaction transaction) {
        return new PaymentTransactionResponse(
                transaction.getId(),
                transaction.getOrder() == null ? null : transaction.getOrder().getId(),
                transaction.getRazorpayOrderId(),
                transaction.getRazorpayPaymentId(),
                transaction.getGatewayStatus(),
                transaction.getPaymentStatus(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getFailureCode(),
                transaction.getFailureDescription(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt()
        );
    }
}
