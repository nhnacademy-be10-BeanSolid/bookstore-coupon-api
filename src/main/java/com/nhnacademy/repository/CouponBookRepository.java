package com.nhnacademy.repository;

import com.nhnacademy.domain.CouponBook;
import com.nhnacademy.repository.queryfactory.CouponBookRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CouponBookRepository extends JpaRepository<CouponBook, Long>, CouponBookRepositoryCustom {

    List<CouponBook> findByCouponPolicy_CouponId(Long couponId);

    boolean existsByCouponPolicy_CouponIdAndBookId(Long couponPolicyId, Long bookId);

    @Query("SELECT cb.bookId FROM CouponBook cb WHERE cb.couponPolicy.couponId = :couponId")
    List<Long> findBookIdsByCouponId(@Param("couponId") Long couponId);

    void deleteByCouponPolicy(com.nhnacademy.domain.CouponPolicy couponPolicy);

    @Query("SELECT COUNT(cb) > 0 FROM CouponBook cb WHERE cb.couponPolicy.couponId = :couponPolicyId AND cb.bookId IN :bookIds")
    boolean existsByCouponPolicyIdAndBookIdsIn(@Param("couponPolicyId") Long couponPolicyId, @Param("bookIds") List<Long> bookIds);
}