package com.nhnacademy.controller;

import com.nhnacademy.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
