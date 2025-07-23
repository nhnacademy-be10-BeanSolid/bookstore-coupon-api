package com.nhnacademy.common.handler;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ErrorMessage {

    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final String path;
    private final String message;

    public ErrorMessage(int status, String error, String path, String message) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.path = path;
        this.message = message;
    }
}