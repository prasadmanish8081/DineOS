package com.dineos.exception;

import org.springframework.http.HttpStatus;

public class RestaurantForbiddenException extends ApiException {

    public RestaurantForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
