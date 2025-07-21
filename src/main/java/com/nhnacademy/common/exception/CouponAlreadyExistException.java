package com.nhnacademy.common.exception;

public class CouponAlreadyExistException extends ConflictException {
    public CouponAlreadyExistException(String message) {
        super(message);
    }
}
