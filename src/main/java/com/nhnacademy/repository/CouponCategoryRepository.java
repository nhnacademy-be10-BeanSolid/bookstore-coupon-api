package com.nhnacademy.repository;

import com.nhnacademy.domain.CouponCategory;
import com.nhnacademy.repository.queryfactory.CouponCategoryRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import java.util.List;

public interface CouponCategoryRepository extends JpaRepository<CouponCategory, Long>,
        QuerydslPredicateExecutor<CouponCategory>,
        CouponCategoryRepositoryCustom {
    List<CouponCategory> findByCouponId(Long couponId);
}