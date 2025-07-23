package com.nhnacademy.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class IssueCategoryCouponRequestDto {
    private Long userId;
    private Long categoryId;
    private Long couponPolicyId;
}
