package com.nhnacademy.repository.queryfactory;

import com.nhnacademy.domain.UserCoupon;

import java.time.LocalDateTime;
import java.util.List;

public interface UserCouponRepositoryCustom {
    List<UserCoupon> findActiveCouponsByUserIdAndPeriod(Long userNo, LocalDateTime startDate, LocalDateTime endDate);
    List<UserCoupon> findUsedCouponsByUserId(Long userNo);
    List<UserCoupon> findExpiredCouponsByUserId(Long userNo);

}
