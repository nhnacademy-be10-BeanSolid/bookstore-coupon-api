package com.nhnacademy.controller.dto;

import com.nhnacademy.domain.UsedCoupon;
import com.nhnacademy.domain.UserCouponStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;

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
    private int couponDiscountAmount;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS") // 추가
    private LocalDateTime issuedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS") // 추가
    private LocalDateTime expiredAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS") // 추가
    private LocalDateTime usedAt;
    private UserCouponStatus status;
    private Long orderId;

    public static UserCouponResponse from(UsedCoupon usedCoupon) {
        return UserCouponResponse.builder()
                .userCouponId(usedCoupon.getUserCouponId())
                .userNo(usedCoupon.getUserNo())
                .couponPolicyId(usedCoupon.getCouponPolicy().getCouponId())
                .couponName(usedCoupon.getCouponPolicy().getCouponName())
                .couponDiscountAmount(usedCoupon.getCouponPolicy().getCouponDiscountAmount())
                .issuedAt(usedCoupon.getIssuedAt())
                .expiredAt(usedCoupon.getExpiredAt())
                .usedAt(usedCoupon.getUsedAt())
                .status(usedCoupon.getStatus())
                .orderId(usedCoupon.getOrderId())
                .build();
    }
}