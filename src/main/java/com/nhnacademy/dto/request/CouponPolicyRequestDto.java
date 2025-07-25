package com.nhnacademy.dto.request;

import com.nhnacademy.domain.enumtype.CouponDiscountType;
import com.nhnacademy.domain.enumtype.CouponScope;
import com.nhnacademy.domain.enumtype.CouponType;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
public class CouponPolicyRequestDto {
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

    @NotNull
    private CouponType couponType;

    private List<Long> bookIds;
    private List<Long> categoryIds;
}
