package com.nhnacademy.controller;

import com.nhnacademy.common.exception.ValidationFailedException;
import com.nhnacademy.domain.CouponPolicy;
import com.nhnacademy.domain.enumtype.CouponScope;
import com.nhnacademy.dto.response.CouponPolicyResponseDto;
import com.nhnacademy.dto.request.CouponPolicyRequestDto;
import com.nhnacademy.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class CouponAdminController {

    private final CouponService couponService;

    @PostMapping("/issue-all/{couponPolicyId}")
    public ResponseEntity<Void> startIssuingCouponsToAllUsers(@PathVariable Long couponPolicyId) {
        couponService.startCouponIssuingProcess(couponPolicyId);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/issue-book")
    public ResponseEntity<Void> issueCouponToBook(@RequestParam Long couponPolicyId, @RequestParam Long bookId) {
        couponService.issueCouponToBook(couponPolicyId, bookId);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/issue-to-user")
    public ResponseEntity<Void> issueCouponToUser(@RequestParam Long userNo, @RequestParam Long couponPolicyId) {
        couponService.issueCouponToUser(userNo, couponPolicyId);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/coupon-policies")
    public ResponseEntity<Void> createCouponPolicy(@Valid @RequestBody CouponPolicyRequestDto request, BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            throw new ValidationFailedException(bindingResult);
        }

        couponService.createCouponPolicy(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/coupon-policies")
    public ResponseEntity<List<CouponPolicyResponseDto>> getAllCouponPolicies() {
        List<CouponPolicyResponseDto> policies = couponService.getAllCouponPolicies();
        return ResponseEntity.ok(policies);
    }

    @GetMapping("/coupon-policies/{couponId}")
    public ResponseEntity<CouponPolicyResponseDto> getCouponPolicyById(@PathVariable Long couponId) {
        Optional<CouponPolicy> policy = couponService.getCouponPolicyById(couponId);
        return policy.map(p -> {
            List<Long> bookIds = null;
            List<Long> categoryIds = null;
            if (p.getCouponScope() == CouponScope.BOOK) {
                bookIds = couponService.getBookIdsByCouponId(p.getCouponId());
            } else if (p.getCouponScope() == CouponScope.CATEGORY) {
                categoryIds = couponService.getCategoryIdsByCouponId(p.getCouponId());
            }
            return ResponseEntity.ok(CouponPolicyResponseDto.builder()
                    .couponId(p.getCouponId())
                    .couponName(p.getCouponName())
                    .couponDiscountType(p.getCouponDiscountType())
                    .couponDiscountAmount(p.getCouponDiscountAmount())
                    .couponMinimumOrderAmount(p.getCouponMinimumOrderAmount())
                    .couponMaximumDiscountAmount(p.getCouponMaximumDiscountAmount())
                    .couponScope(p.getCouponScope())
                    .couponExpiredAt(p.getCouponExpiredAt())
                    .couponIssuePeriod(p.getCouponIssuePeriod())
                    .couponType(p.getCouponType())
                    .couponCreatedAt(p.getCouponCreatedAt())
                    .bookIds(bookIds)
                    .categoryIds(categoryIds)
                    .build());
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/coupon-policies/{couponId}")
    public ResponseEntity<Void> deleteCouponPolicy(@PathVariable Long couponId) {
        couponService.deleteCouponPolicy(couponId);
        return ResponseEntity.noContent().build();
    }

}
