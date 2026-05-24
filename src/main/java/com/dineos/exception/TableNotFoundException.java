package com.dineos.exception;

import org.springframework.http.HttpStatus;

public class TableNotFoundException extends ApiException {

    public TableNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
