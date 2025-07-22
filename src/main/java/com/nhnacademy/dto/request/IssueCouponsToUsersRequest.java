package com.nhnacademy.dto.request;

import java.util.List;

public record IssueCouponsToUsersRequest(
    Long couponPolicyId,
    List<Long> userNos
) {}
