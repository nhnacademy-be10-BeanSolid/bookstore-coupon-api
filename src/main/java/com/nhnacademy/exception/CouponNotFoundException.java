package com.nhnacademy.exception;

import com.nhnacademy.common.exception.NotFoundException;

public class CouponNotFoundException extends NotFoundException {
    public CouponNotFoundException(String message) {
        super(message);
    }
}
