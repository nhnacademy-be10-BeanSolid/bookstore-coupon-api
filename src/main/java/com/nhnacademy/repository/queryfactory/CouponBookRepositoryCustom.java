package com.nhnacademy.repository.queryfactory;

import java.util.List;

public interface CouponBookRepositoryCustom {
    boolean existsByCouponPolicyIdAndBookIdsIn(Long couponPolicyId, List<Long> bookIds);
}