package com.dineos.exception;

import org.springframework.http.HttpStatus;

public class QrCodeGenerationException extends ApiException {

    public QrCodeGenerationException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
