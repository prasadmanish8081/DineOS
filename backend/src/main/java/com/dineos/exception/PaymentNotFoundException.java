package com.dineos.exception;

import org.springframework.http.HttpStatus;

public class PaymentNotFoundException extends ApiException {

    public PaymentNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
