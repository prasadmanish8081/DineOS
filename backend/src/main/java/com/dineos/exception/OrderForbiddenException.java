package com.dineos.exception;

import org.springframework.http.HttpStatus;

public class OrderForbiddenException extends ApiException {

    public OrderForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
