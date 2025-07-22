package com.nhnacademy.common.exception;

public class BadRequestException extends CustomHttpException {
    public BadRequestException(String message) {
        super(CustomHttpStatus.BAD_REQUEST, message);
    }
}
