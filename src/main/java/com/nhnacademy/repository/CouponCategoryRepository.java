package com.nhnacademy.repository;

import com.nhnacademy.domain.CouponCategory;
import com.nhnacademy.repository.queryfactory.CouponCategoryRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CouponCategoryRepository extends JpaRepository<CouponCategory, Long>,
        QuerydslPredicateExecutor<CouponCategory>,
        CouponCategoryRepositoryCustom {
    List<CouponCategory> findByCouponId(Long couponId);

    @Query("SELECT cc.categoryId FROM CouponCategory cc WHERE cc.couponPolicy.couponId = :couponId")
    List<Long> findCategoryIdsByCouponId(@Param("couponId") Long couponId);
}