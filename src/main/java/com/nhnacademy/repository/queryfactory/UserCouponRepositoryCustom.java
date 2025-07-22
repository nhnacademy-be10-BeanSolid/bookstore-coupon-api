package com.nhnacademy.repository.queryfactory;

import com.nhnacademy.domain.UserCouponList;

import java.time.LocalDateTime;
import java.util.List;

public interface UserCouponRepositoryCustom {
    List<UserCouponList> findActiveCouponsByUserIdAndPeriod(Long userNo, LocalDateTime startDate, LocalDateTime endDate);
    List<UserCouponList> findUsedCouponsByUserId(Long userNo);
    List<UserCouponList> findExpiredCouponsByUserId(Long userNo);

}
