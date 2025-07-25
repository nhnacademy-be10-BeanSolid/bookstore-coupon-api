package com.nhnacademy.repository.impl;

import com.nhnacademy.domain.*;
import com.nhnacademy.domain.enumtype.CouponScope;
import com.nhnacademy.domain.enumtype.CouponType;
import com.nhnacademy.domain.enumtype.UserCouponStatus;
import com.nhnacademy.repository.queryfactory.CouponPolicyRepositoryCustom;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class CouponPolicyRepositoryImpl implements CouponPolicyRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public CouponPolicyRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Optional<CouponPolicy> findByCouponType(CouponType couponType) {
        QCouponPolicy couponPolicy = QCouponPolicy.couponPolicy;

        return Optional.ofNullable(queryFactory
                .selectFrom(couponPolicy)
                .where(couponPolicy.couponType.eq(couponType))
                .fetchOne());
    }

    @Override
    public List<CouponPolicy> findApplicableCouponPolicies(Long userNo, int orderAmount, List<Long> bookIdsInOrder, List<Long> categoryIdsInOrder) {
        QCouponPolicy policy = QCouponPolicy.couponPolicy;
        QUserCouponList userCouponList = QUserCouponList.userCouponList;

        BooleanExpression commonConditions = policy.couponMinimumOrderAmount.loe(orderAmount)
                .and(policy.couponExpiredAt.after(LocalDateTime.now()));

        BooleanExpression scopeConditions = policy.couponScope.eq(CouponScope.ALL);

        if (bookIdsInOrder != null && !bookIdsInOrder.isEmpty()) {
            QCouponBook couponBook = QCouponBook.couponBook;
            scopeConditions = scopeConditions.or(
                    policy.couponScope.eq(CouponScope.BOOK)
                            .and(queryFactory.selectFrom(couponBook)
                                    .where(couponBook.couponPolicy.eq(policy).and(couponBook.bookId.in(bookIdsInOrder)))
                                    .exists())
            );
        }
        if (categoryIdsInOrder != null && !categoryIdsInOrder.isEmpty()) {
            QCouponCategory couponCategory = QCouponCategory.couponCategory;
            scopeConditions = scopeConditions.or(
                    policy.couponScope.eq(CouponScope.CATEGORY)
                            .and(queryFactory.selectFrom(couponCategory)
                                    .where(couponCategory.couponPolicy.eq(policy).and(couponCategory.categoryId.in(categoryIdsInOrder)))
                                    .exists())
            );
        }

        return queryFactory
                .select(policy)
                .from(policy)
                .join(userCouponList).on(userCouponList.couponPolicy.eq(policy))
                .where(userCouponList.userNo.eq(userNo)
                        .and(userCouponList.status.eq(UserCouponStatus.ACTIVE))
                        .and(userCouponList.expiredAt.after(LocalDateTime.now()))
                        .and(commonConditions)
                        .and(scopeConditions)
                )
                .fetch();
    }
}
