package com.nhnacademy.repository.queryfactory;

import java.util.List;

public interface CouponCategoryRepositoryCustom {
    boolean existsByCouponPolicyIdAndCategoryIdsIn(Long couponPolicyId, List<Long> categoryIds);
}