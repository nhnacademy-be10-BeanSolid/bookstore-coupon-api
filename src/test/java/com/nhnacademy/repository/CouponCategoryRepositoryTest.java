package com.nhnacademy.repository;

import com.nhnacademy.domain.CouponCategory;
import com.nhnacademy.domain.CouponPolicy;
import com.nhnacademy.domain.enumtype.CouponDiscountType;
import com.nhnacademy.domain.enumtype.CouponScope;
import com.nhnacademy.domain.enumtype.CouponType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CouponCategoryRepositoryTest {

    @Autowired
    private CouponCategoryRepository couponCategoryRepository;

    @Autowired
    private CouponPolicyRepository couponPolicyRepository;

    @Test
    @DisplayName("CouponCategory 저장 및 CouponPolicy 기반 조회")
    void testSaveAndFindByCouponPolicyId() {
        // 쿠폰 정책 저장
        CouponPolicy policy = CouponPolicy.builder()
                .couponName("Category Coupon")
                .couponType(CouponType.GENERAL)
                .couponDiscountType(CouponDiscountType.FIXED)
                .couponDiscountAmount(3000)
                .couponMinimumOrderAmount(20000)
                .couponMaximumDiscountAmount(5000)
                .couponScope(CouponScope.CATEGORY)
                .couponExpiredAt(LocalDateTime.now().plusDays(30))
                .couponIssuePeriod(null)
                .build();

        CouponPolicy savedPolicy = couponPolicyRepository.save(policy);

        // 쿠폰 카테고리 2개 저장
        CouponCategory cc1 = new CouponCategory(null, savedPolicy, 10L);
        CouponCategory cc2 = new CouponCategory(null, savedPolicy, 20L);
        couponCategoryRepository.saveAll(Arrays.asList(cc1, cc2));

        // 조회 테스트
        List<CouponCategory> list = couponCategoryRepository.findByCouponPolicy_CouponId(savedPolicy.getCouponId());
        assertThat(list).hasSize(2);
        assertThat(list).extracting("categoryId").containsExactlyInAnyOrder(10L, 20L);
    }

    @Test
    @DisplayName("existsByCouponPolicyIdAndCategoryIdsIn 테스트 - 정상 동작")
    void testExistsByCouponPolicyIdAndCategoryIdsIn() {
        // 쿠폰 정책 저장
        CouponPolicy policy = CouponPolicy.builder()
                .couponName("Check Exists Coupon")
                .couponType(CouponType.GENERAL)
                .couponDiscountType(CouponDiscountType.FIXED)
                .couponDiscountAmount(2000)
                .couponMinimumOrderAmount(15000)
                .couponMaximumDiscountAmount(3000)
                .couponScope(CouponScope.CATEGORY)
                .couponExpiredAt(LocalDateTime.now().plusDays(15))
                .couponIssuePeriod(null)
                .build();

        CouponPolicy savedPolicy = couponPolicyRepository.save(policy);

        // CouponCategory 저장
        CouponCategory cc = new CouponCategory(null, savedPolicy, 30L);
        couponCategoryRepository.save(cc);

        // 1) 포함하는 categoryId 를 넘겼을 때 true 기대
        boolean existsTrue = couponCategoryRepository.existsByCouponPolicyIdAndCategoryIdsIn(savedPolicy.getCouponId(), List.of(30L, 999L));
        assertThat(existsTrue).isTrue();

        // 2) 포함하지 않는 categoryId 만 넘겼을 때 false 기대
        boolean existsFalse = couponCategoryRepository.existsByCouponPolicyIdAndCategoryIdsIn(savedPolicy.getCouponId(), List.of(888L, 777L));
        assertThat(existsFalse).isFalse();

        // 3) null 혹은 빈 리스트 넘어갈 때 false 기대
        boolean existsNull = couponCategoryRepository.existsByCouponPolicyIdAndCategoryIdsIn(savedPolicy.getCouponId(), null);
        boolean existsEmpty = couponCategoryRepository.existsByCouponPolicyIdAndCategoryIdsIn(savedPolicy.getCouponId(), List.of());
        assertThat(existsNull).isFalse();
        assertThat(existsEmpty).isFalse();
    }

    @Test
    @DisplayName("findCategoryIdsByCouponId 테스트")
    void testFindCategoryIdsByCouponId() {
        CouponPolicy policy = CouponPolicy.builder()
                .couponName("Find CategoryIds Coupon")
                .couponType(CouponType.GENERAL)
                .couponDiscountType(CouponDiscountType.PERCENT)
                .couponDiscountAmount(10)
                .couponMinimumOrderAmount(25000)
                .couponMaximumDiscountAmount(4000)
                .couponScope(CouponScope.CATEGORY)
                .couponExpiredAt(LocalDateTime.now().plusDays(60))
                .couponIssuePeriod(null)
                .build();

        CouponPolicy savedPolicy = couponPolicyRepository.save(policy);

        CouponCategory cc1 = new CouponCategory(null, savedPolicy, 100L);
        CouponCategory cc2 = new CouponCategory(null, savedPolicy, 101L);

        couponCategoryRepository.saveAll(List.of(cc1, cc2));

        List<Long> categoryIds = couponCategoryRepository.findCategoryIdsByCouponId(savedPolicy.getCouponId());

        assertThat(categoryIds).hasSize(2)
                .containsExactlyInAnyOrder(100L, 101L);
    }

    @Test
    @DisplayName("deleteByCouponPolicy 테스트")
    void testDeleteByCouponPolicy() {
        CouponPolicy policy = CouponPolicy.builder()
                .couponName("Delete Category Coupon")
                .couponType(CouponType.GENERAL)
                .couponDiscountType(CouponDiscountType.PERCENT)
                .couponDiscountAmount(5)
                .couponMinimumOrderAmount(2000)
                .couponMaximumDiscountAmount(500)
                .couponScope(CouponScope.CATEGORY)
                .couponExpiredAt(LocalDateTime.now().plusDays(20))
                .couponIssuePeriod(null)
                .build();

        CouponPolicy savedPolicy = couponPolicyRepository.save(policy);

        CouponCategory cc = new CouponCategory(null, savedPolicy, 200L);
        couponCategoryRepository.save(cc);

        couponCategoryRepository.deleteByCouponPolicy(savedPolicy);

        List<CouponCategory> list = couponCategoryRepository.findByCouponPolicy_CouponId(savedPolicy.getCouponId());
        assertThat(list).isEmpty();
    }
}
