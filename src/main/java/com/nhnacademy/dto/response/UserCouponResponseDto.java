package com.nhnacademy.dto.response;


import com.nhnacademy.domain.UserCouponList;
import com.nhnacademy.domain.enumtype.UserCouponStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCouponResponseDto {
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

    public static UserCouponResponseDto from(UserCouponList userCoupon) {
        Long couponPolicyId = null;
        String couponName = null;
        int couponDiscountAmount = 0;

        if (userCoupon.getCouponPolicy() != null) {
            couponPolicyId = userCoupon.getCouponPolicy().getCouponId();
            couponName = userCoupon.getCouponPolicy().getCouponName();
            couponDiscountAmount = userCoupon.getCouponPolicy().getCouponDiscountAmount();
        }

        return UserCouponResponseDto.builder()
                .userCouponId(userCoupon.getUserCouponId())
                .userNo(userCoupon.getUserNo())
                .couponPolicyId(couponPolicyId)
                .couponName(couponName)
                .couponDiscountAmount(couponDiscountAmount)
                .issuedAt(userCoupon.getIssuedAt())
                .expiredAt(userCoupon.getExpiredAt())
                .usedAt(userCoupon.getUsedAt())
                .status(userCoupon.getStatus())
                .orderId(userCoupon.getOrderId())
                .build();
    }
}