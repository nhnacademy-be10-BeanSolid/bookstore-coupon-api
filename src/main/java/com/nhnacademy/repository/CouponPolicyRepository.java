package com.nhnacademy.repository;

import com.nhnacademy.domain.CouponPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface CouponPolicyRepository extends JpaRepository<CouponPolicy, Long>,
        QuerydslPredicateExecutor<CouponPolicy>,
        CouponPolicyRepositoryCustom {

}