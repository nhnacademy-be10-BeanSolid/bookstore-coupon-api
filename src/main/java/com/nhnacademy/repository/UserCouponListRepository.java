package com.nhnacademy.repository;

import com.nhnacademy.domain.CouponPolicy;
import com.nhnacademy.domain.UserCouponList;
import com.nhnacademy.domain.enumtype.UserCouponStatus;
import com.nhnacademy.repository.queryfactory.UserCouponListRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserCouponListRepository extends JpaRepository<UserCouponList, Long>, UserCouponListRepositoryCustom {

    //추후에 사용 예정
//    List<UserCouponList> findByUserNo(Long userNo);
//
//    List<UserCouponList> findByStatusAndExpiredAtBefore(UserCouponStatus status, LocalDateTime dateTime);

    List<UserCouponList> findByUserNoAndCouponPolicy(Long userNo, CouponPolicy couponPolicy);

    Optional<UserCouponList> findByUserNoAndUserCouponId(Long userNo, Long userCouponId);

    void deleteByCouponPolicy(CouponPolicy couponPolicy);

    List<UserCouponList> findByUserNoAndStatus(Long userNo, UserCouponStatus userCouponStatus);
}
