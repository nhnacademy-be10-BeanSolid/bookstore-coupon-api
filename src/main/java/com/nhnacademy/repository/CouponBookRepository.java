package com.nhnacademy.repository;

import com.nhnacademy.domain.CouponBook;
import com.nhnacademy.repository.queryfactory.CouponBookRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import java.util.List;

public interface CouponBookRepository extends JpaRepository<CouponBook, Long>,
        QuerydslPredicateExecutor<CouponBook>,
        CouponBookRepositoryCustom {
    List<CouponBook> findByCouponId(Long couponId);
}