package com.nhnacademy.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class IssueBookCouponRequestDto {
    private Long userId;
    private Long bookId;
    private Long couponPolicyId;
}
