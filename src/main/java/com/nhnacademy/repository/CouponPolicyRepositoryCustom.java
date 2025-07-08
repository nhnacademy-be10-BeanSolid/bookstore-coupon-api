package com.nhnacademy.repository;

import com.nhnacademy.domain.CouponPolicy;

import java.util.List;
import java.util.Optional;

public interface CouponPolicyRepositoryCustom {

    // 쿠폰 정책을 조회하는 QueryDSL 메소드 (Welcome, Birthday 쿠폰 정책 조회 시 활용)
    Optional<CouponPolicy> findByName(String couponName);

    List<CouponPolicy> findApplicableCouponPolicies(String userNo, int orderAmount, List<Long> bookIdsInOrder, List<Long> categoryIdsInOrder);
}