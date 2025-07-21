package com.nhnacademy.dto;

import com.nhnacademy.domain.CouponDiscountType;
import com.nhnacademy.domain.CouponScope;
import com.nhnacademy.domain.CouponType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponPolicyResponseDto {
    private Long couponId;
    private String couponName;
    private CouponDiscountType couponDiscountType;
    private int couponDiscountAmount;
    private Integer couponMinimumOrderAmount;
    private Integer couponMaximumDiscountAmount;
    private CouponScope couponScope;
    private LocalDateTime couponExpiredAt;
    private Integer couponIssuePeriod;
    private CouponType couponType;
    private LocalDateTime couponCreatedAt;
    private List<Long> bookIds;
    private List<Long> categoryIds;
}
