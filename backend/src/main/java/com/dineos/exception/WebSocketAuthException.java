package com.dineos.exception;

import org.springframework.http.HttpStatus;

public class WebSocketAuthException extends ApiException {

    public WebSocketAuthException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
