package com.dineos.dto.request;

import jakarta.validation.constraints.NotBlank;

public record VerifyRazorpayPaymentRequest(
        @NotBlank(message = "Razorpay payment id is required")
        String razorpayPaymentId,

        @NotBlank(message = "Razorpay order id is required")
        String razorpayOrderId,

        @NotBlank(message = "Razorpay signature is required")
        String razorpaySignature
) {
}
