package com.dineos.exception;

import org.springframework.http.HttpStatus;

public class TableForbiddenException extends ApiException {

    public TableForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
