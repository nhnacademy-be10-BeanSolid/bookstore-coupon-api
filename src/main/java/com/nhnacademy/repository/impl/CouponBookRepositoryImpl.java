package com.nhnacademy.repository.impl;

import com.nhnacademy.domain.QCouponBook;
import com.nhnacademy.repository.CouponBookRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CouponBookRepositoryImpl implements CouponBookRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public CouponBookRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public boolean existsByCouponPolicyIdAndBookIdsIn(Long couponPolicyId, List<Long> bookIds) {
        if (bookIds == null || bookIds.isEmpty()) {
            return false;
        }
        QCouponBook couponBook = QCouponBook.couponBook;
        return queryFactory.selectOne()
                .from(couponBook)
                .where(couponBook.couponPolicy.couponId.eq(couponPolicyId)
                        .and(couponBook.bookId.in(bookIds)))
                .fetchFirst() != null;
    }
}