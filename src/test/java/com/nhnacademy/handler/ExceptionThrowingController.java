package com.nhnacademy.handler; // GlobalExceptionHandlerTest와 같은 패키지에 위치

import com.nhnacademy.exception.*; // 당신의 예외 클래스들 import
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/test-exception")
public class ExceptionThrowingController {

    @GetMapping("/coupon-not-found")
    public void throwCouponNotFoundException() {
        throw new CouponNotFoundException("테스트: 쿠폰을 찾을 수 없음.");
    }

    @GetMapping("/user-coupon-not-found")
    public void throwUserCouponNotFoundException() {
        throw new UserCouponNotFoundException("테스트: 사용자 쿠폰을 찾을 수 없음.");
    }

    @GetMapping("/coupon-already-used")
    public void throwCouponAlreadyUsedException() {
        throw new CouponAlreadyUsedException("테스트: 이미 사용된 쿠폰.");
    }

    @GetMapping("/coupon-expired")
    public void throwCouponExpiredException() {
        throw new CouponExpiredException("테스트: 만료된 쿠폰.");
    }

    @GetMapping("/coupon-not-applicable")
    public void throwCouponNotApplicableException() {
        throw new CouponNotApplicableException("테스트: 쿠폰 적용 불가.");
    }

    @GetMapping("/welcome-coupon-policy-not-found")
    public void throwWelcomeCouponPolicyNotFoundException() {
        throw new WelcomeCouponPolicyNotFoundException("테스트: 웰컴 쿠폰 정책을 찾을 수 없음.");
    }

    @GetMapping("/runtime-exception")
    public void throwRuntimeException() {
        throw new RuntimeException("테스트: 예상치 못한 런타임 오류.");
    }

    @PostMapping("/validation-error")
    public void throwMethodArgumentNotValidException(@Valid @RequestBody TestValidationDto dto) {
    }
}