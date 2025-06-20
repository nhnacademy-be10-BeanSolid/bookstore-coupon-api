package com.nhnacademy.handler;

import com.nhnacademy.exception.*;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CouponNotFoundException.class)
    public ResponseEntity<String> handleCouponNotFoundException(CouponNotFoundException e) {
        log.warn("쿠폰을 찾을 수 없음: {}", e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserCouponNotFoundException.class)
    public ResponseEntity<String> handleUserCouponNotFoundException(UserCouponNotFoundException e) {
        log.warn("사용자 쿠폰을 찾을 수 없음: {}", e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CouponAlreadyUsedException.class)
    public ResponseEntity<String> handleCouponAlreadyUsedException(CouponAlreadyUsedException e) {
        log.warn("이미 사용된 쿠폰: {}", e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CouponExpiredException.class)
    public ResponseEntity<String> handleCouponExpiredException(CouponExpiredException e) {
        log.warn("만료된 쿠폰: {}", e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CouponNotApplicableException.class)
    public ResponseEntity<String> handleCouponNotApplicableException(CouponNotApplicableException e) {
        log.warn("쿠폰 적용 불가: {}", e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.warn("유효성 검사 실패: {}", errors);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WelcomeCouponPolicyNotFoundException.class)
    public ResponseEntity<String> handleWelcomeCouponPolicyNotFoundException(WelcomeCouponPolicyNotFoundException e) {
        log.warn("웰컴 쿠폰 정책을 찾을 수 없음: {}", e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException ex) {
        log.warn("비즈니스 로직 충돌/상태 오류: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleAllRuntimeExceptions(RuntimeException e) {
        log.error("예상치 못한 오류 발생: {}", e.getMessage(), e);
        return new ResponseEntity<>("알 수 없는 서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}