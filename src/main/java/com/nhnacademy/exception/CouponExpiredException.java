package com.nhnacademy.exception;

import com.nhnacademy.common.exception.BadRequestException;

public class CouponExpiredException extends BadRequestException {
    public CouponExpiredException(String message) {
        super(message);
    }
}