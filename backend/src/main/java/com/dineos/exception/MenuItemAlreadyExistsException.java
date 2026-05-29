package com.dineos.exception;

import org.springframework.http.HttpStatus;

public class MenuItemAlreadyExistsException extends ApiException {

    public MenuItemAlreadyExistsException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
