package com.nhnacademy.exception;

import com.nhnacademy.common.exception.NotFoundException;

public class UserCouponNotFoundException extends NotFoundException {
    public UserCouponNotFoundException(String message) {
        super(message);
    }
}