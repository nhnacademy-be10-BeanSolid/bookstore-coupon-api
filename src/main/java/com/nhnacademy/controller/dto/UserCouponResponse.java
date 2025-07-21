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
    private Long userNo;
    private Long couponPolicyId;
    private String couponName;
    private int couponDiscountAmount;

    private LocalDateTime issuedAt;

    private LocalDateTime expiredAt;

    private LocalDateTime usedAt;
    private UserCouponStatus status;
    private Long orderId;

    public static UserCouponResponse from(UsedCoupon usedCoupon) {
        Long couponPolicyId = null;
        String couponName = null;
        int couponDiscountAmount = 0;

        if (usedCoupon.getCouponPolicy() != null) {
            couponPolicyId = usedCoupon.getCouponPolicy().getCouponId();
            couponName = usedCoupon.getCouponPolicy().getCouponName();
            couponDiscountAmount = usedCoupon.getCouponPolicy().getCouponDiscountAmount();
        }

        return UserCouponResponse.builder()
                .userCouponId(usedCoupon.getUserCouponId())
                .userNo(usedCoupon.getUserNo())
                .couponPolicyId(couponPolicyId)
                .couponName(couponName)
                .couponDiscountAmount(couponDiscountAmount)
                .issuedAt(usedCoupon.getIssuedAt())
                .expiredAt(usedCoupon.getExpiredAt())
                .usedAt(usedCoupon.getUsedAt())
                .status(usedCoupon.getStatus())
                .orderId(usedCoupon.getOrderId())
                .build();
    }
}