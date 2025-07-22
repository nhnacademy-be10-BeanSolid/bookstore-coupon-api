package com.nhnacademy.repository;

import com.nhnacademy.domain.CouponPolicy;
import com.nhnacademy.domain.UsedCoupon;
import com.nhnacademy.domain.UserCouponStatus;
import com.nhnacademy.repository.queryfactory.UserCouponRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserCouponRepository extends JpaRepository<UsedCoupon, Long>,
        QuerydslPredicateExecutor<UsedCoupon>,
        UserCouponRepositoryCustom {

    //추후에 사용 예정
//    List<UsedCoupon> findByUserNo(Long userNo);
//
//    List<UsedCoupon> findByStatusAndExpiredAtBefore(UserCouponStatus status, LocalDateTime dateTime);

    List<UsedCoupon> findByUserNoAndCouponPolicy(Long userNo, CouponPolicy couponPolicy);

    Optional<UsedCoupon> findByUserNoAndUserCouponId(Long userNo, Long userCouponId);

    void deleteByCouponPolicy(CouponPolicy couponPolicy);
}
