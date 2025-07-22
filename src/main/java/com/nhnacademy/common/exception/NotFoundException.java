package com.nhnacademy.common.exception;

public class NotFoundException extends CustomHttpException {
    public NotFoundException(String message) {
        super(CustomHttpStatus.NOT_FOUND, message);
    }
}
