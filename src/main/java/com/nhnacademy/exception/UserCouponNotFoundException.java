package com.nhnacademy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserCouponNotFoundException extends RuntimeException {
    public UserCouponNotFoundException(String message) {
        super(message);
    }
}