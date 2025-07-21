package com.nhnacademy.controller.dto;

import java.util.List;

public record IssueCouponsToUsersRequest(
    Long couponPolicyId,
    List<Long> userNos
) {}
