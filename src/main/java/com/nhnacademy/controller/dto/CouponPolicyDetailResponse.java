package com.nhnacademy.controller.dto;

import com.nhnacademy.domain.CouponDiscountType;
import com.nhnacademy.domain.CouponScope;
import com.nhnacademy.domain.CouponPolicy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class CouponPolicyDetailResponse {
    private Long couponId;
    private String couponName;
    private CouponDiscountType couponDiscountType;
    private int couponDiscountAmount;
    private Integer couponMinimumOrderAmount;
    private Integer couponMaximumDiscountAmount;
    private CouponScope couponScope;
    private LocalDateTime couponExpiredAt;
    private Integer couponIssuePeriod;
    private List<Long> bookIds;
    private List<Long> categoryIds;

    public static CouponPolicyDetailResponse from(CouponPolicy policy, List<Long> bookIds, List<Long> categoryIds) {
        return CouponPolicyDetailResponse.builder()
                .couponId(policy.getCouponId())
                .couponName(policy.getCouponName())
                .couponDiscountType(policy.getCouponDiscountType())
                .couponDiscountAmount(policy.getCouponDiscountAmount())
                .couponMinimumOrderAmount(policy.getCouponMinimumOrderAmount())
                .couponMaximumDiscountAmount(policy.getCouponMaximumDiscountAmount())
                .couponScope(policy.getCouponScope())
                .couponExpiredAt(policy.getCouponExpiredAt())
                .couponIssuePeriod(policy.getCouponIssuePeriod())
                .bookIds(bookIds)
                .categoryIds(categoryIds)
                .build();
    }
}
