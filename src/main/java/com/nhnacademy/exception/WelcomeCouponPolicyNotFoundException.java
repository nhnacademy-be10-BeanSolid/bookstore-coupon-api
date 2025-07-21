package com.nhnacademy.exception;

import com.nhnacademy.common.exception.NotFoundException;

public class WelcomeCouponPolicyNotFoundException extends NotFoundException {
    public WelcomeCouponPolicyNotFoundException(String message) {
        super(message);
    }
}