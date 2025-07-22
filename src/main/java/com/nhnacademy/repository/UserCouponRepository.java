package com.nhnacademy.repository;

import com.nhnacademy.domain.CouponPolicy;
import com.nhnacademy.domain.UserCoupon;
import com.nhnacademy.repository.queryfactory.UserCouponRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long>, UserCouponRepositoryCustom {

    //추후에 사용 예정
//    List<UsedCoupon> findByUserNo(Long userNo);
//
//    List<UsedCoupon> findByStatusAndExpiredAtBefore(UserCouponStatus status, LocalDateTime dateTime);

    List<UserCoupon> findByUserNoAndCouponPolicy(Long userNo, CouponPolicy couponPolicy);

    Optional<UserCoupon> findByUserNoAndUserCouponId(Long userNo, Long userCouponId);

    void deleteByCouponPolicy(CouponPolicy couponPolicy);
}
