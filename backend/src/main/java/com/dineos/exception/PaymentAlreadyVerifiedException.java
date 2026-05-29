package com.dineos.exception;

import org.springframework.http.HttpStatus;

public class PaymentAlreadyVerifiedException extends ApiException {

    public PaymentAlreadyVerifiedException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
