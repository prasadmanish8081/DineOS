package com.dineos.exception;

import org.springframework.http.HttpStatus;

public class InvalidOrderStatusTransitionException extends ApiException {

    public InvalidOrderStatusTransitionException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
