package com.nhnacademy.repository;

import com.nhnacademy.domain.CouponBook;
import com.nhnacademy.repository.queryfactory.CouponBookRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CouponBookRepository extends JpaRepository<CouponBook, Long>,
        QuerydslPredicateExecutor<CouponBook>,
        CouponBookRepositoryCustom {
    List<CouponBook> findByCouponId(Long couponId);

    boolean existsByCouponPolicy_CouponIdAndBookId(Long couponPolicyId, Long bookId);

    @Query("SELECT cb.bookId FROM CouponBook cb WHERE cb.couponId = :couponId")
    List<Long> findBookIdsByCouponId(@Param("couponId") Long couponId);
}