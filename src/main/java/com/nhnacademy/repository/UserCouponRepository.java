package com.nhnacademy.repository;

import com.nhnacademy.domain.CouponPolicy;
import com.nhnacademy.domain.UsedCoupon;
import com.nhnacademy.domain.UserCouponStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserCouponRepository extends JpaRepository<UsedCoupon, Long> {

    List<UsedCoupon> findByUserId(String userId);

    List<UsedCoupon> findByStatusAndExpiredAtBefore(UserCouponStatus status, LocalDateTime dateTime);

    List<UsedCoupon> findByUserIdAndCouponPolicy(String userId, CouponPolicy couponPolicy);

    Optional<UsedCoupon> findByUserIdAndUserCouponId(String userId, Long userCouponId);
}