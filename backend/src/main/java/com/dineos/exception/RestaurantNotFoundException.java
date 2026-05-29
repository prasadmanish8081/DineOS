package com.dineos.exception;

import org.springframework.http.HttpStatus;

public class RestaurantNotFoundException extends ApiException {

    public RestaurantNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
