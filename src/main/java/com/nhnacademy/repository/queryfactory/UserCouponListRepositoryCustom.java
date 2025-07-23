package com.nhnacademy.repository.queryfactory;

import com.nhnacademy.domain.UserCouponList;

import java.util.List;

public interface UserCouponListRepositoryCustom {
    List<UserCouponList> findActiveCouponsByUserNo(Long userNo);
    List<UserCouponList> findUsedCouponsByUserNo(Long userNo);
    List<UserCouponList> findExpiredCouponsByUserNo(Long userNo);

}
