package com.nhnacademy.repository;

import com.nhnacademy.domain.CouponDiscountType;
import com.nhnacademy.domain.CouponPolicy;
import com.nhnacademy.domain.CouponScope;
import com.nhnacademy.domain.CouponType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CouponPolicyRepositoryTest {

    @Autowired
    private CouponPolicyRepository couponPolicyRepository;

    @Test
    @DisplayName("쿠폰 정책 저장 및 조회")
    void testSaveAndFindById() {
        CouponPolicy policy = CouponPolicy.builder()
                .couponName("10% Discount Coupon")
                .couponType(CouponType.GENERAL)
                .couponDiscountType(CouponDiscountType.PERCENT)
                .couponDiscountAmount(10)
                .couponMinimumOrderAmount(20000)
                .couponMaximumDiscountAmount(3000)
                .couponScope(CouponScope.ALL)
                .couponExpiredAt(LocalDateTime.now().plusDays(60))
                .couponIssuePeriod(30)
                .build();

        CouponPolicy savedPolicy = couponPolicyRepository.save(policy);
        CouponPolicy foundPolicy = couponPolicyRepository.findById(savedPolicy.getCouponId()).orElse(null);

        assertThat(savedPolicy).isNotNull();
        assertThat(savedPolicy.getCouponId()).isNotNull();
        assertThat(foundPolicy).isNotNull();
        assertThat(foundPolicy.getCouponName()).isEqualTo("10% Discount Coupon");
        assertThat(foundPolicy.getCouponScope()).isEqualTo(CouponScope.ALL);
    }
}
