package com.nhnacademy.controller;

import com.nhnacademy.common.exception.ValidationFailedException;
import com.nhnacademy.domain.CouponPolicy;
import com.nhnacademy.domain.UserCouponList;
import com.nhnacademy.dto.request.CouponPolicyRequestDto;
import com.nhnacademy.dto.request.CouponUseRequestDto;
import com.nhnacademy.dto.request.IssueBookCouponRequestDto;
import com.nhnacademy.dto.request.IssueCategoryCouponRequestDto;
import com.nhnacademy.dto.response.CouponPolicyResponseDto;
import com.nhnacademy.dto.response.UserCouponResponseDto;
import com.nhnacademy.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.nhnacademy.controller.swagger.CouponControllerDoc;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController implements CouponControllerDoc {

    private final CouponService couponService;

    @PostMapping("/policy")
    public ResponseEntity<CouponPolicy> createCouponPolicy(@Valid @RequestBody CouponPolicyRequestDto request, BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            throw new ValidationFailedException(bindingResult);
        }

        return new ResponseEntity<>(couponService.createCouponPolicy(request), HttpStatus.CREATED);
    }

    @GetMapping("/policy")
    public ResponseEntity<List<CouponPolicyResponseDto>> getAllCouponPolicies() {

        return new ResponseEntity<>(couponService.getAllCouponPolicies(), HttpStatus.OK);
    }

    @GetMapping("/policy/{policyId}")
    public ResponseEntity<CouponPolicy> getCouponPolicy(@PathVariable Long policyId) {

        return new ResponseEntity<>(couponService.getCouponPolicy(policyId), HttpStatus.OK);
    }

    @PostMapping("/users/{userNo}/issue/{couponPolicyId}")
    public ResponseEntity<UserCouponResponseDto> issueCouponToUser(@PathVariable Long userNo, @PathVariable Long couponPolicyId) {
        UserCouponList issuedCoupon = couponService.issueCouponToUser(userNo, couponPolicyId);
        return new ResponseEntity<>(UserCouponResponseDto.from(issuedCoupon), HttpStatus.CREATED);
    }

    @PostMapping("/users/{userNo}/issue-welcome")
    public ResponseEntity<UserCouponResponseDto> issueWelcomeCoupon(@PathVariable Long userNo) {
        UserCouponList welcomeCoupon = couponService.issueWelcomeCoupon(userNo);
        return new ResponseEntity<>(UserCouponResponseDto.from(welcomeCoupon), HttpStatus.CREATED);
    }

    @PostMapping("/users/{userNo}/issue-birthday")
    public ResponseEntity<UserCouponResponseDto> issueBirthdayCoupon(@PathVariable Long userNo,
                                                                     @RequestParam int birthMonth) {
        LocalDate userBirthDate = LocalDate.now().withMonth(birthMonth).withDayOfMonth(1);
        UserCouponList birthdayCoupon = couponService.issueBirthdayCoupon(userNo, userBirthDate);
        return new ResponseEntity<>(UserCouponResponseDto.from(birthdayCoupon), HttpStatus.CREATED);
    }

    @PostMapping("/issue/book")
    public ResponseEntity<UserCouponResponseDto> issueBookCoupon(@RequestBody IssueBookCouponRequestDto request) {
        UserCouponList issuedCoupon = couponService.issueBookCoupon(request);
        return new ResponseEntity<>(UserCouponResponseDto.from(issuedCoupon), HttpStatus.CREATED);
    }

    @PostMapping("/issue/category")
    public ResponseEntity<UserCouponResponseDto> issueCategoryCoupon(@RequestBody IssueCategoryCouponRequestDto request) {
        UserCouponList issuedCoupon = couponService.issueCategoryCoupon(request.getUserId(), request.getCouponPolicyId(), request.getCategoryId());
        return new ResponseEntity<>(UserCouponResponseDto.from(issuedCoupon), HttpStatus.CREATED);
    }

    @GetMapping("/users/{userNo}/active")
    public ResponseEntity<List<UserCouponResponseDto>> getActiveUserCoupons(@PathVariable Long userNo) {
        log.info("Coupon-API CouponController: Received request for active coupons for userNo: {}", userNo);
        List<UserCouponList> activeCoupons = couponService.getActiveUserCoupons(userNo);
        List<UserCouponResponseDto> responses = activeCoupons.stream()
                .map(UserCouponResponseDto::from)
                .toList();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/users/{userNo}/used")
    public ResponseEntity<List<UserCouponResponseDto>> getUsedUserCoupons(@PathVariable Long userNo) {
        List<UserCouponList> userCoupons = couponService.getUsedUserCoupons(userNo);
        List<UserCouponResponseDto> responses = userCoupons.stream()
                .map(UserCouponResponseDto::from)
                .toList();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PostMapping("/users/{userNo}/use/{userCouponId}")
    public ResponseEntity<String> useCoupon(
            @PathVariable Long userNo,
            @PathVariable Long userCouponId,
            @RequestBody CouponUseRequestDto request) {
        couponService.useCoupon(userNo, userCouponId, request.getOrderId());
        return new ResponseEntity<>("쿠폰이 성공적으로 사용되었습니다.", HttpStatus.OK);
    }

    @GetMapping("/users/{userNo}/calculate-discount/{userCouponId}")
    public ResponseEntity<Integer> calculateDiscount(
            @PathVariable Long userNo,
            @PathVariable Long userCouponId,
            @RequestParam int orderAmount,
            @RequestParam(required = false) List<Long> bookIds,
            @RequestParam(required = false) List<Long> categoryIds) {
        int discount = couponService.calculateDiscountAmount(userNo, userCouponId, orderAmount, bookIds, categoryIds);
        return new ResponseEntity<>(discount, HttpStatus.OK);
    }
}