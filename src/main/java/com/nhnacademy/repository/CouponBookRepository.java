package com.nhnacademy.repository;

import com.nhnacademy.domain.CouponBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponBookRepository extends JpaRepository<CouponBook, Long> {
    List<CouponBook> findByCouponId(Long couponId);
}
