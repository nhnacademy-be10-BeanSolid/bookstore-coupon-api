package com.nhnacademy.repository;

import com.nhnacademy.domain.CouponPolicy;
import com.nhnacademy.domain.UsedCoupon;
import com.nhnacademy.domain.UserCouponStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserCouponRepository extends JpaRepository<UsedCoupon, Long>,
        QuerydslPredicateExecutor<UsedCoupon>,
        UserCouponRepositoryCustom {

    List<UsedCoupon> findByUserNo(String userNo);

    List<UsedCoupon> findByStatusAndExpiredAtBefore(UserCouponStatus status, LocalDateTime dateTime);

    List<UsedCoupon> findByUserNoAndCouponPolicy(String userNo, CouponPolicy couponPolicy);

    Optional<UsedCoupon> findByUserNoAndUserCouponId(String userNo, Long userCouponId);
}
