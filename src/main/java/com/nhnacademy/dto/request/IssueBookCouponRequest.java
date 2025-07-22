package com.nhnacademy.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IssueBookCouponRequest {
    private Long userId;
    private Long bookId;
    private Long couponPolicyId;
}
