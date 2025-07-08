package com.nhnacademy.controller;

import com.nhnacademy.controller.dto.CouponPolicyRequest;
import com.nhnacademy.controller.dto.CouponUseRequest;
import com.nhnacademy.controller.dto.UserCouponResponse;
import com.nhnacademy.domain.CouponPolicy;
import com.nhnacademy.domain.UsedCoupon;
import com.nhnacademy.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/coupons")
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

    @PostMapping("/users/{userNo}/issue/{couponPolicyId}")
    public ResponseEntity<UserCouponResponse> issueCouponToUser(@PathVariable String userNo, @PathVariable Long couponPolicyId) {
        UsedCoupon issuedCoupon = couponService.issueCouponToUser(userNo, couponPolicyId);
        return new ResponseEntity<>(UserCouponResponse.from(issuedCoupon), HttpStatus.CREATED);
    }

    @PostMapping("/users/{userNo}/issue-welcome")
    public ResponseEntity<UserCouponResponse> issueWelcomeCoupon(@PathVariable String userNo) {
        UsedCoupon welcomeCoupon = couponService.issueWelcomeCoupon(userNo);
        return new ResponseEntity<>(UserCouponResponse.from(welcomeCoupon), HttpStatus.CREATED);
    }

    @PostMapping("/users/{userNo}/issue-birthday")
    public ResponseEntity<UserCouponResponse> issueBirthdayCoupon(@PathVariable String userNo,
                                                                  @RequestParam int birthMonth) {
        LocalDate userBirthDate = LocalDate.now().withMonth(birthMonth).withDayOfMonth(1);
        UsedCoupon birthdayCoupon = couponService.issueBirthdayCoupon(userNo, userBirthDate);
        return new ResponseEntity<>(UserCouponResponse.from(birthdayCoupon), HttpStatus.CREATED);
    }

    @GetMapping("/users/{userNo}/active")
    public ResponseEntity<List<UserCouponResponse>> getActiveUserCoupons(@PathVariable String userNo) {
        List<UsedCoupon> activeCoupons = couponService.getActiveUserCoupons(userNo);
        List<UserCouponResponse> responses = activeCoupons.stream()
                .map(UserCouponResponse::from)
                .toList();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/users/{userNo}/used")
    public ResponseEntity<List<UserCouponResponse>> getUsedUserCoupons(@PathVariable String userNo) {
        List<UsedCoupon> usedCoupons = couponService.getUsedUserCoupons(userNo);
        List<UserCouponResponse> responses = usedCoupons.stream()
                .map(UserCouponResponse::from)
                .toList();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PostMapping("/users/{userNo}/use/{userCouponId}")
    public ResponseEntity<String> useCoupon(
            @PathVariable String userNo,
            @PathVariable Long userCouponId,
            @RequestBody CouponUseRequest request) {
        couponService.useCoupon(userNo, userCouponId, request.getOrderId());
        return new ResponseEntity<>("쿠폰이 성공적으로 사용되었습니다.", HttpStatus.OK);
    }

    @GetMapping("/users/{userNo}/calculate-discount/{userCouponId}")
    public ResponseEntity<Integer> calculateDiscount(
            @PathVariable String userNo,
            @PathVariable Long userCouponId,
            @RequestParam int orderAmount,
            @RequestParam(required = false) List<Long> bookIds,
            @RequestParam(required = false) List<Long> categoryIds) {
        int discount = couponService.calculateDiscountAmount(userNo, userCouponId, orderAmount, bookIds, categoryIds);
        return new ResponseEntity<>(discount, HttpStatus.OK);
    }
}