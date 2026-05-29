package com.dineos.dto.request;

import jakarta.validation.constraints.NotBlank;

public record FailedRazorpayPaymentRequest(
        @NotBlank(message = "Razorpay payment id is required")
        String razorpayPaymentId,

        @NotBlank(message = "Razorpay order id is required")
        String razorpayOrderId,

        String errorCode,

        String errorDescription
) {
}
