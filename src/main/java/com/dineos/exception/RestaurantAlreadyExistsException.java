package com.dineos.exception;

import org.springframework.http.HttpStatus;

public class RestaurantAlreadyExistsException extends ApiException {

    public RestaurantAlreadyExistsException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
