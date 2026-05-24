package com.dineos.exception;

import org.springframework.http.HttpStatus;

public class CategoryNotFoundException extends ApiException {

    public CategoryNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
