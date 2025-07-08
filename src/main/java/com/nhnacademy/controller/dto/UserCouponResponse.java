package com.nhnacademy.controller.dto;

import com.nhnacademy.domain.UsedCoupon;
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
    private String userNo;
    private Long couponPolicyId;
    private String couponName;
    private LocalDateTime issuedAt;
    private LocalDateTime expiredAt;
    private LocalDateTime usedAt;
    private UserCouponStatus status;
    private Long orderId;

    public static UserCouponResponse from(UsedCoupon usedCoupon) {
        return UserCouponResponse.builder()
                .userCouponId(usedCoupon.getUserCouponId())
                .userNo(usedCoupon.getUserNo())
                .couponPolicyId(usedCoupon.getCouponPolicy().getCouponId())
                .couponName(usedCoupon.getCouponPolicy().getCouponName())
                .issuedAt(usedCoupon.getIssuedAt())
                .expiredAt(usedCoupon.getExpiredAt())
                .usedAt(usedCoupon.getUsedAt())
                .status(usedCoupon.getStatus())
                .orderId(usedCoupon.getOrderId())
                .build();
    }
}