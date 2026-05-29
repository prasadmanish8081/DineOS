package com.dineos.exception;

import org.springframework.http.HttpStatus;

public class CategoryAlreadyExistsException extends ApiException {

    public CategoryAlreadyExistsException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
