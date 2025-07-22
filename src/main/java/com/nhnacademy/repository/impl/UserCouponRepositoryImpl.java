package com.nhnacademy.repository.impl;

import com.nhnacademy.domain.QUserCouponList;
import com.nhnacademy.domain.UserCouponList;
import com.nhnacademy.domain.enumtype.UserCouponStatus;
import com.nhnacademy.repository.queryfactory.UserCouponRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class UserCouponRepositoryImpl implements UserCouponRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public UserCouponRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<UserCouponList> findActiveCouponsByUserIdAndPeriod(Long userNo, LocalDateTime startDate, LocalDateTime endDate) {
        QUserCouponList usedCoupon = QUserCouponList.userCouponList;

        return queryFactory
                .selectFrom(usedCoupon)
                .where(usedCoupon.userNo.eq(userNo)
                        .and(usedCoupon.status.eq(UserCouponStatus.ACTIVE))
                        .and(usedCoupon.expiredAt.after(LocalDateTime.now())))
                .fetch();
    }

    @Override
    public List<UserCouponList> findUsedCouponsByUserId(Long userNo) {
        QUserCouponList usedCoupon = QUserCouponList.userCouponList;

        return queryFactory
                .selectFrom(usedCoupon)
                .where(usedCoupon.userNo.eq(userNo)
                        .and(usedCoupon.status.eq(UserCouponStatus.USED)))
                .fetch();
    }

    @Override
    public List<UserCouponList> findExpiredCouponsByUserId(Long userNo) {
        QUserCouponList usedCoupon = QUserCouponList.userCouponList;

        return queryFactory
                .selectFrom(usedCoupon)
                .where(usedCoupon.userNo.eq(userNo)
                        .and(usedCoupon.status.eq(UserCouponStatus.EXPIRED)))
                .fetch();
    }
}
