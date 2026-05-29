package com.dineos.exception;

import org.springframework.http.HttpStatus;

public class AnalyticsAccessDeniedException extends ApiException {

    public AnalyticsAccessDeniedException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
