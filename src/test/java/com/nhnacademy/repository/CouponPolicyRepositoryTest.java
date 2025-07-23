package com.nhnacademy.repository;

import com.nhnacademy.domain.CouponBook;
import com.nhnacademy.domain.CouponCategory;
import com.nhnacademy.domain.CouponPolicy;
import com.nhnacademy.domain.UserCouponList;
import com.nhnacademy.domain.enumtype.CouponDiscountType;
import com.nhnacademy.domain.enumtype.CouponScope;
import com.nhnacademy.domain.enumtype.CouponType;
import com.nhnacademy.domain.enumtype.UserCouponStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CouponPolicyRepositoryTest {

    @Autowired
    private CouponPolicyRepository couponPolicyRepository;

    @Autowired
    private CouponBookRepository couponBookRepository;

    @Autowired
    private CouponCategoryRepository couponCategoryRepository;

    @Autowired
    private UserCouponListRepository userCouponListRepository;

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
        Optional<CouponPolicy> foundPolicyOptional =
                couponPolicyRepository.findById(savedPolicy.getCouponId());

        assertThat(savedPolicy).isNotNull();
        assertThat(savedPolicy.getCouponId()).isNotNull();
        assertThat(foundPolicyOptional).isPresent();

        CouponPolicy foundPolicy = foundPolicyOptional.get();
        assertThat(foundPolicy.getCouponName()).isEqualTo("10% Discount Coupon");
        assertThat(foundPolicy.getCouponScope()).isEqualTo(CouponScope.ALL);
    }

    @Test
    @DisplayName("쿠폰 정책 타입으로 조회")
    void testFindByCouponType() {
        CouponPolicy policy = CouponPolicy.builder()
                .couponName("General Coupon")
                .couponType(CouponType.GENERAL)
                .couponDiscountType(CouponDiscountType.PERCENT)
                .couponDiscountAmount(15)
                .couponMinimumOrderAmount(25000)
                .couponMaximumDiscountAmount(5000)
                .couponScope(CouponScope.ALL)
                .couponExpiredAt(LocalDateTime.now().plusDays(30))
                .couponIssuePeriod(10)
                .build();

        couponPolicyRepository.save(policy);

        Optional<CouponPolicy> found =
                couponPolicyRepository.findByCouponType(CouponType.GENERAL);

        assertThat(found).isPresent();
        assertThat(found.get().getCouponName()).isEqualTo("General Coupon");
    }

    @Test
    @DisplayName("적용 가능한 쿠폰 정책 리스트 조회 - book 및 category 리스트 empty")
    void testFindApplicableCouponPoliciesNotEmpty() {
        CouponPolicy policy = CouponPolicy.builder()
                .couponName("Applicable Coupon")
                .couponType(CouponType.GENERAL)
                .couponDiscountType(CouponDiscountType.PERCENT)
                .couponDiscountAmount(20)
                .couponMinimumOrderAmount(10000)
                .couponMaximumDiscountAmount(4000)
                .couponScope(CouponScope.ALL)
                .couponExpiredAt(LocalDateTime.now().plusDays(10))
                .couponIssuePeriod(10)
                .build();

        couponPolicyRepository.save(policy);

        Long userNo = 1L;
        int orderAmount = 15000;
        List<Long> bookIdsInOrder = Collections.emptyList();
        List<Long> categoryIdsInOrder = Collections.emptyList();

        List<CouponPolicy> applicablePolicies =
                couponPolicyRepository.findApplicableCouponPolicies(userNo, orderAmount, bookIdsInOrder, categoryIdsInOrder);

        assertThat(applicablePolicies).isEmpty();
    }

    @Test
    @DisplayName("적용 가능한 쿠폰 정책 리스트 조회 - book 및 category 리스트 not empty")
    void testFindApplicableCouponPolicies() {
        CouponPolicy policy = CouponPolicy.builder()
                .couponName("Applicable Coupon")
                .couponType(CouponType.GENERAL)
                .couponDiscountType(CouponDiscountType.PERCENT)
                .couponDiscountAmount(20)
                .couponMinimumOrderAmount(10000)
                .couponMaximumDiscountAmount(4000)
                .couponScope(CouponScope.ALL)
                .couponExpiredAt(LocalDateTime.now().plusDays(10))
                .couponIssuePeriod(10)
                .build();

        couponPolicyRepository.save(policy);

        couponBookRepository.save(new CouponBook(1L, policy, 2L));

        couponCategoryRepository.save(new CouponCategory(1L, policy, 3L));

        UserCouponList userCoupon = UserCouponList.builder()
                .userNo(1L)
                .couponPolicy(policy)
                .status(UserCouponStatus.ACTIVE)
                .expiredAt(LocalDateTime.now().plusDays(5))  // 만료 전
                .build();

        userCouponListRepository.save(userCoupon);

        Long userNo = 1L;
        int orderAmount = 15000;
        List<Long> bookIdsInOrder = List.of(2L);
        List<Long> categoryIdsInOrder = List.of(3L);

        List<CouponPolicy> applicablePolicies =
                couponPolicyRepository.findApplicableCouponPolicies(userNo, orderAmount, bookIdsInOrder, categoryIdsInOrder);

        assertThat(applicablePolicies).isNotEmpty();
        assertThat(applicablePolicies)
                .extracting(CouponPolicy::getCouponName)
                .contains("Applicable Coupon");

    }
}
