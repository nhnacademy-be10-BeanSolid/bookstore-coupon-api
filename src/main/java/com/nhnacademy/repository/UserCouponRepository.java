package com.nhnacademy.repository;

import com.nhnacademy.domain.CouponPolicy;
import com.nhnacademy.domain.UserCoupon;
import com.nhnacademy.domain.UserCouponStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

    List<UserCoupon> findByUserId(Long userId);

    List<UserCoupon> findByStatusAndExpiredAtBefore(UserCouponStatus status, LocalDateTime dateTime);

    List<UserCoupon> findByUserIdAndCouponPolicy(Long userId, CouponPolicy couponPolicy);

    Optional<UserCoupon> findByUserIdAndUserCouponId(Long userId, Long userCouponId);
}