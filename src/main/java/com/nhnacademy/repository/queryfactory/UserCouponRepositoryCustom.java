package com.nhnacademy.repository.queryfactory;

import com.nhnacademy.domain.UsedCoupon;

import java.time.LocalDateTime;
import java.util.List;

public interface UserCouponRepositoryCustom {
    List<UsedCoupon> findActiveCouponsByUserIdAndPeriod(Long userNo, LocalDateTime startDate, LocalDateTime endDate);
    List<UsedCoupon> findUsedCouponsByUserId(Long userNo);
    List<UsedCoupon> findExpiredCouponsByUserId(Long userNo);

}
