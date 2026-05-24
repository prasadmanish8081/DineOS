package com.dineos.exception;

import org.springframework.http.HttpStatus;

public class OrderAlreadyExistsException extends ApiException {

    public OrderAlreadyExistsException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
