package com.nhnacademy.controller.dto;

import com.nhnacademy.domain.UserCoupon;
import com.nhnacademy.domain.UserCouponStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCouponResponse {
    private Long userCouponId;
    private String userId;
    private Long couponPolicyId;
    private String couponName;
    private LocalDateTime issuedAt;
    private LocalDateTime expiredAt;
    private LocalDateTime usedAt;
    private UserCouponStatus status;

    public static UserCouponResponse from(UserCoupon userCoupon) {
        return UserCouponResponse.builder()
                .userCouponId(userCoupon.getUserCouponId())
                .userId(userCoupon.getUserId())
                .couponPolicyId(userCoupon.getCouponPolicy().getCouponId())
                .couponName(userCoupon.getCouponPolicy().getCouponName())
                .issuedAt(userCoupon.getIssuedAt())
                .expiredAt(userCoupon.getExpiredAt())
                .usedAt(userCoupon.getUsedAt())
                .status(userCoupon.getStatus())
                .build();
    }
}