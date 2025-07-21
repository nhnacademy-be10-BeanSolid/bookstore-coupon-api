package com.nhnacademy.exception;

import com.nhnacademy.common.exception.BadRequestException;

public class CouponNotApplicableException extends BadRequestException {
    public CouponNotApplicableException(String message) {
        super(message);
    }
}