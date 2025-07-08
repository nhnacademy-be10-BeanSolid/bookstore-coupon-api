package com.nhnacademy.controller.dto;

import com.nhnacademy.domain.CouponDiscountType;
import com.nhnacademy.domain.CouponScope;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CouponPolicyRequest {
    @NotBlank
    private String couponName;

    @NotNull
    private CouponDiscountType couponDiscountType;

    @Min(0)
    private Integer couponDiscountAmount;

    @Min(0)
    private Integer couponMinimumOrderAmount;

    @Min(0)
    private Integer couponMaximumDiscountAmount;

    @NotNull
    private CouponScope couponScope;

    private LocalDateTime couponExpiredAt;

    private Integer couponIssuePeriod;

    private List<Long> bookIds;
    private List<Long> categoryIds;
}
