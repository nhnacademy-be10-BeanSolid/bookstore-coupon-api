package com.nhnacademy.repository;

import java.util.List;

public interface CouponCategoryRepositoryCustom {
    boolean existsByCouponPolicyIdAndCategoryIdsIn(Long couponPolicyId, List<Long> categoryIds);
}