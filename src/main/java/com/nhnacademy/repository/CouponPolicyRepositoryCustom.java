package com.nhnacademy.repository;

import com.nhnacademy.domain.CouponPolicy;
import com.nhnacademy.domain.CouponType;

import java.util.List;
import java.util.Optional;

public interface CouponPolicyRepositoryCustom {

    // 쿠폰 정책을 유형별로 조회하는 QueryDSL 메소드
    Optional<CouponPolicy> findByCouponType(CouponType couponType);

    List<CouponPolicy> findApplicableCouponPolicies(String userNo, int orderAmount, List<Long> bookIdsInOrder, List<Long> categoryIdsInOrder);
}