package com.nhnacademy.repository.impl;

import com.nhnacademy.domain.QUserCouponList;
import com.nhnacademy.domain.UserCouponList;
import com.nhnacademy.domain.enumtype.UserCouponStatus;
import com.nhnacademy.repository.queryfactory.UserCouponListRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class UserCouponListRepositoryImpl implements UserCouponListRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public UserCouponListRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<UserCouponList> findActiveCouponsByUserNo(Long userNo) {
        QUserCouponList usedCoupon = QUserCouponList.userCouponList;

        return queryFactory
                .selectFrom(usedCoupon)
                .where(usedCoupon.userNo.eq(userNo)
                        .and(usedCoupon.status.eq(UserCouponStatus.ACTIVE))
                        .and(usedCoupon.expiredAt.after(LocalDateTime.now())))
                .fetch();
    }

    @Override
    public List<UserCouponList> findUsedCouponsByUserNo(Long userNo) {
        QUserCouponList usedCoupon = QUserCouponList.userCouponList;

        return queryFactory
                .selectFrom(usedCoupon)
                .where(usedCoupon.userNo.eq(userNo)
                        .and(usedCoupon.status.eq(UserCouponStatus.USED)))
                .fetch();
    }

    @Override
    public List<UserCouponList> findExpiredCouponsByUserNo(Long userNo) {
        QUserCouponList usedCoupon = QUserCouponList.userCouponList;

        return queryFactory
                .selectFrom(usedCoupon)
                .where(usedCoupon.userNo.eq(userNo)
                        .and(usedCoupon.status.eq(UserCouponStatus.EXPIRED)))
                .fetch();
    }
}
