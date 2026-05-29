package com.dineos.exception;

import org.springframework.http.HttpStatus;

public class PaymentGatewayConfigurationException extends ApiException {

    public PaymentGatewayConfigurationException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
