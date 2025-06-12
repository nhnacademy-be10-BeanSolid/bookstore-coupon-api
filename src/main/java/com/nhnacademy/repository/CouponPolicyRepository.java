package com.nhnacademy.repository;

import com.nhnacademy.domain.CouponPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CouponPolicyRepository extends JpaRepository<CouponPolicy, Long> {
    Optional<CouponPolicy> findByCouponName(String couponName);
}