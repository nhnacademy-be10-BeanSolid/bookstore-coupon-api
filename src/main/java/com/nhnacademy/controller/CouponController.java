package com.nhnacademy.controller;

import com.nhnacademy.controller.dto.CouponPolicyRequest;
import com.nhnacademy.controller.dto.UserCouponResponse;
import com.nhnacademy.domain.CouponPolicy;
import com.nhnacademy.domain.UserCoupon;
import com.nhnacademy.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping("/policy")
    public ResponseEntity<CouponPolicy> createCouponPolicy(@Valid @RequestBody CouponPolicyRequest request) {
        CouponPolicy newPolicy = couponService.createCouponPolicy(
                request.getCouponName(),
                request.getCouponDiscountType(),
                request.getCouponDiscountAmount(),
                request.getCouponMinimumOrderAmount(),
                request.getCouponMaximumDiscountAmount(),
                request.getCouponScope(),
                request.getCouponExpiredAt(),
                request.getCouponIssuePeriod(),
                request.getBookIds(),
                request.getCategoryIds()
        );
        return new ResponseEntity<>(newPolicy, HttpStatus.CREATED);
    }

    @GetMapping("/policy")
    public ResponseEntity<List<CouponPolicy>> getAllCouponPolicies() {
        List<CouponPolicy> policies = couponService.getAllCouponPolicies();
        return new ResponseEntity<>(policies, HttpStatus.OK);
    }

    @GetMapping("/policy/{policyId}")
    public ResponseEntity<CouponPolicy> getCouponPolicy(@PathVariable Long policyId) {
        CouponPolicy policy = couponService.getCouponPolicy(policyId);
        return new ResponseEntity<>(policy, HttpStatus.OK);
    }

    @PostMapping("/users/{userId}/issue/{couponPolicyId}")
    public ResponseEntity<UserCouponResponse> issueCouponToUser(@PathVariable Long userId, @PathVariable Long couponPolicyId) {
        UserCoupon issuedCoupon = couponService.issueCouponToUser(userId, couponPolicyId);
        return new ResponseEntity<>(UserCouponResponse.from(issuedCoupon), HttpStatus.CREATED);
    }

    @PostMapping("/users/{userId}/issue-welcome")
    public ResponseEntity<UserCouponResponse> issueWelcomeCoupon(@PathVariable Long userId) {
        UserCoupon welcomeCoupon = couponService.issueWelcomeCoupon(userId);
        return new ResponseEntity<>(UserCouponResponse.from(welcomeCoupon), HttpStatus.CREATED);
    }

    @PostMapping("/users/{userId}/issue-birthday")
    public ResponseEntity<UserCouponResponse> issueBirthdayCoupon(@PathVariable Long userId,
                                                                  @RequestParam int birthMonth) {
        UserCoupon birthdayCoupon = couponService.issueBirthdayCoupon(userId, birthMonth);
        return new ResponseEntity<>(UserCouponResponse.from(birthdayCoupon), HttpStatus.CREATED);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<UserCouponResponse>> getUserCoupons(@PathVariable Long userId) {
        List<UserCoupon> userCoupons = couponService.getUserCoupons(userId);
        List<UserCouponResponse> responses = userCoupons.stream()
                .map(UserCouponResponse::from)
                .collect(Collectors.toList());
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PostMapping("/users/{userId}/use/{userCouponId}")
    public ResponseEntity<String> useCoupon(@PathVariable Long userId, @PathVariable Long userCouponId) {
        couponService.useCoupon(userId, userCouponId);
        return new ResponseEntity<>("쿠폰이 성공적으로 사용되었습니다.", HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/calculate-discount/{userCouponId}")
    public ResponseEntity<Integer> calculateDiscount(
            @PathVariable Long userId,
            @PathVariable Long userCouponId,
            @RequestParam int orderAmount,
            @RequestParam(required = false) List<Long> bookIds,
            @RequestParam(required = false) List<Long> categoryIds) {
        int discount = couponService.calculateDiscountAmount(userId, userCouponId, orderAmount, bookIds, categoryIds);
        return new ResponseEntity<>(discount, HttpStatus.OK);
    }
}