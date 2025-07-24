package com.nhnacademy.repository;

import com.nhnacademy.domain.CouponCategory;
import com.nhnacademy.domain.CouponPolicy;
import com.nhnacademy.repository.queryfactory.CouponCategoryRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CouponCategoryRepository extends JpaRepository<CouponCategory, Long>, CouponCategoryRepositoryCustom {

    List<CouponCategory> findByCouponPolicy_CouponId(Long couponId);

    @Query("SELECT cc.categoryId FROM CouponCategory cc WHERE cc.couponPolicy.couponId = :couponId")
    List<Long> findCategoryIdsByCouponId(@Param("couponId") Long couponId);

    @Modifying(clearAutomatically = true)
    @Query("delete From CouponCategory cc where cc.couponPolicy = :couponPolicy")
    void deleteByCouponPolicy(CouponPolicy couponPolicy);

    boolean existsByCouponPolicy_CouponIdAndCategoryId(Long couponPolicyId, Long categoryId);

    @Query("SELECT COUNT(cc) > 0 FROM CouponCategory cc WHERE cc.couponPolicy.couponId = :couponPolicyId AND cc.categoryId IN :categoryIds")
    boolean existsByCouponPolicyIdAndCategoryIdsIn(@Param("couponPolicyId") Long couponPolicyId, @Param("categoryIds") List<Long> categoryIds);
}