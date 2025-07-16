package com.nhnacademy.repository.impl;

import com.nhnacademy.domain.QUsedCoupon;
import com.nhnacademy.domain.UserCouponStatus;
import com.nhnacademy.repository.UserCouponRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import com.nhnacademy.domain.UsedCoupon;

@Repository
public class UserCouponRepositoryImpl implements UserCouponRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public UserCouponRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<UsedCoupon> findActiveCouponsByUserIdAndPeriod(String userNo, LocalDateTime startDate, LocalDateTime endDate) {
        QUsedCoupon usedCoupon = QUsedCoupon.usedCoupon;

        return queryFactory
                .selectFrom(usedCoupon)
                .where(usedCoupon.userNo.eq(userNo)
                        .and(usedCoupon.status.eq(UserCouponStatus.ACTIVE))
                        .and(usedCoupon.issuedAt.between(startDate, endDate))
                        .and(usedCoupon.expiredAt.after(LocalDateTime.now())))
                .fetch();
    }

    @Override
    public List<UsedCoupon> findUsedCouponsByUserId(String userNo) {
        QUsedCoupon usedCoupon = QUsedCoupon.usedCoupon;

        return queryFactory
                .selectFrom(usedCoupon)
                .where(usedCoupon.userNo.eq(userNo)
                        .and(usedCoupon.status.eq(UserCouponStatus.USED)))
                .fetch();
    }

    @Override
    public List<UsedCoupon> findExpiredCouponsByUserId(String userNo) {
        QUsedCoupon usedCoupon = QUsedCoupon.usedCoupon;

        return queryFactory
                .selectFrom(usedCoupon)
                .where(usedCoupon.userNo.eq(userNo)
                        .and(usedCoupon.status.eq(UserCouponStatus.EXPIRED)))
                .fetch();
    }
}
