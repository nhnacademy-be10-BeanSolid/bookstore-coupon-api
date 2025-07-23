package com.nhnacademy.dto.request;

import java.util.List;

public record IssueCouponsToUsersRequestDto(
    Long couponPolicyId,
    List<Long> userNos
) {}
