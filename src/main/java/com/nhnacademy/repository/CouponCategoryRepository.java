package com.nhnacademy.repository;

import com.nhnacademy.domain.CouponCategory;
import com.nhnacademy.domain.CouponCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CouponCategoryRepository extends JpaRepository<CouponCategory, CouponCategoryId> {
    List<CouponCategory> findByCouponId(Long couponId);
}
