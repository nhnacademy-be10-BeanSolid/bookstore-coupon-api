package com.nhnacademy.exception;

import com.nhnacademy.common.exception.BadRequestException;

public class CouponAlreadyUsedException extends BadRequestException {
    public CouponAlreadyUsedException(String message) {
        super(message);
    }
}