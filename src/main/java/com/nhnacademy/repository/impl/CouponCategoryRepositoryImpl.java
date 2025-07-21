package com.nhnacademy.repository.impl;

import com.nhnacademy.domain.QCouponCategory;
import com.nhnacademy.repository.queryfactory.CouponCategoryRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CouponCategoryRepositoryImpl implements CouponCategoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public CouponCategoryRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public boolean existsByCouponPolicyIdAndCategoryIdsIn(Long couponPolicyId, List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return false;
        }
        QCouponCategory couponCategory = QCouponCategory.couponCategory;
        return queryFactory.selectOne()
                .from(couponCategory)
                .where(couponCategory.couponPolicy.couponId.eq(couponPolicyId)
                        .and(couponCategory.categoryId.in(categoryIds)))
                .fetchFirst() != null;
    }
}