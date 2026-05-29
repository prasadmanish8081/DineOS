package com.dineos.exception;

import org.springframework.http.HttpStatus;

public class MenuItemNotFoundException extends ApiException {

    public MenuItemNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
