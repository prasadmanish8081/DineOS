package com.dineos.exception;

import org.springframework.http.HttpStatus;

public class TableAlreadyExistsException extends ApiException {

    public TableAlreadyExistsException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
