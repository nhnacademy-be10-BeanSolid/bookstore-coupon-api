package com.nhnacademy.repository.queryfactory;

import com.nhnacademy.domain.UsedCoupon;

import java.time.LocalDateTime;
import java.util.List;

public interface UserCouponRepositoryCustom {
    List<UsedCoupon> findActiveCouponsByUserIdAndPeriod(String userNo, LocalDateTime startDate, LocalDateTime endDate);
    List<UsedCoupon> findUsedCouponsByUserId(String userNo);
    List<UsedCoupon> findExpiredCouponsByUserId(String userNo);

}
