package com.nhnacademy.controller;

import com.nhnacademy.common.exception.ValidationFailedException;
import com.nhnacademy.dto.request.CouponPolicyRequestDto;
import com.nhnacademy.dto.request.CouponUseRequestDto;
import com.nhnacademy.dto.request.IssueBookCouponRequestDto;
import com.nhnacademy.dto.response.CouponPolicyResponseDto;
import com.nhnacademy.dto.response.UserCouponResponseDto;
import com.nhnacademy.domain.CouponPolicy;
import com.nhnacademy.controller.swagger.CouponControllerDoc;
import com.nhnacademy.domain.UserCouponList;
import com.nhnacademy.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController implements CouponControllerDoc {

    private final CouponService couponService;

    @Override
    @PostMapping("/policy")
    public ResponseEntity<CouponPolicy> createCouponPolicy(@Valid @RequestBody CouponPolicyRequestDto request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException(bindingResult);
        }
        return new ResponseEntity<>(couponService.createCouponPolicy(request), HttpStatus.CREATED);
    }

    @Override
    @GetMapping("/policy")
    public ResponseEntity<List<CouponPolicyResponseDto>> getAllCouponPolicies() {
        return new ResponseEntity<>(couponService.getAllCouponPolicies(), HttpStatus.OK);
    }

    @Override
    @GetMapping("/policy/{policyId}")
    public ResponseEntity<CouponPolicy> getCouponPolicy(@PathVariable Long policyId) {
        return new ResponseEntity<>(couponService.getCouponPolicy(policyId), HttpStatus.OK);
    }

    @Override
    @PostMapping("/users/{userNo}/issue/{couponPolicyId}")
    public ResponseEntity<UserCouponResponseDto> issueCouponToUser(@PathVariable Long userNo, @PathVariable Long couponPolicyId) {
        UserCouponList issuedCoupon = couponService.issueCouponToUser(userNo, couponPolicyId);
        return new ResponseEntity<>(UserCouponResponseDto.from(issuedCoupon), HttpStatus.CREATED);
    }

    @Override
    @PostMapping("/users/{userNo}/issue-welcome")
    public ResponseEntity<UserCouponResponseDto> issueWelcomeCoupon(@PathVariable Long userNo) {
        UserCouponList welcomeCoupon = couponService.issueWelcomeCoupon(userNo);
        return new ResponseEntity<>(UserCouponResponseDto.from(welcomeCoupon), HttpStatus.CREATED);
    }

    @Override
    @PostMapping("/users/{userNo}/issue-birthday")
    public ResponseEntity<UserCouponResponseDto> issueBirthdayCoupon(@PathVariable Long userNo, @RequestParam int birthMonth) {
        LocalDate userBirthDate = LocalDate.now().withMonth(birthMonth).withDayOfMonth(1);
        UserCouponList birthdayCoupon = couponService.issueBirthdayCoupon(userNo, userBirthDate);
        return new ResponseEntity<>(UserCouponResponseDto.from(birthdayCoupon), HttpStatus.CREATED);
    }

    @Override
    @PostMapping("/issue/book")
    public ResponseEntity<UserCouponResponseDto> issueBookCoupon(@RequestBody IssueBookCouponRequestDto request) {
        UserCouponList issuedCoupon = couponService.issueBookCoupon(request);
        return new ResponseEntity<>(UserCouponResponseDto.from(issuedCoupon), HttpStatus.CREATED);
    }

    @Override
    @GetMapping("/users/{userNo}/active")
    public ResponseEntity<List<UserCouponResponseDto>> getActiveUserCoupons(@PathVariable Long userNo) {
        log.info("CouponController: getActiveUserCoupons for userNo {}", userNo);
        List<UserCouponList> activeCoupons = couponService.getActiveUserCoupons(userNo);
        List<UserCouponResponseDto> responses = activeCoupons.stream().map(UserCouponResponseDto::from).toList();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @Override
    @GetMapping("/users/{userNo}/used")
    public ResponseEntity<List<UserCouponResponseDto>> getUsedUserCoupons(@PathVariable Long userNo) {
        List<UserCouponList> userCoupons = couponService.getUsedUserCoupons(userNo);
        List<UserCouponResponseDto> responses = userCoupons.stream().map(UserCouponResponseDto::from).toList();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @Override
    @PostMapping("/users/{userNo}/use/{userCouponId}")
    public ResponseEntity<String> useCoupon(@PathVariable Long userNo, @PathVariable Long userCouponId, @RequestBody CouponUseRequestDto request) {
        couponService.useCoupon(userNo, userCouponId, request.getOrderId());
        return new ResponseEntity<>("쿠폰이 성공적으로 사용되었습니다.", HttpStatus.OK);
    }

    @Override
    @GetMapping("/users/{userNo}/calculate-discount/{userCouponId}")
    public ResponseEntity<Integer> calculateDiscount(@PathVariable Long userNo,
                                                     @PathVariable Long userCouponId,
                                                     @RequestParam int orderAmount,
                                                     @RequestParam(required = false) List<Long> bookIds,
                                                     @RequestParam(required = false) List<Long> categoryIds) {
        int discount = couponService.calculateDiscountAmount(userNo, userCouponId, orderAmount, bookIds, categoryIds);
        return new ResponseEntity<>(discount, HttpStatus.OK);
    }
}