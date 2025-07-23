package com.nhnacademy.repository;

import com.nhnacademy.domain.CouponBook;
import com.nhnacademy.domain.CouponPolicy;
import com.nhnacademy.domain.enumtype.CouponScope;
import com.nhnacademy.domain.enumtype.CouponType;
import com.nhnacademy.domain.enumtype.CouponDiscountType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CouponBookRepositoryTest {

    @Autowired
    private CouponBookRepository couponBookRepository;

    @Autowired
    private CouponPolicyRepository couponPolicyRepository;

    @Test
    @DisplayName("CouponBook 저장 및 조회")
    void testSaveAndFindByCouponPolicyId() {
        CouponPolicy couponPolicy = CouponPolicy.builder()
                .couponName("Test Coupon")
                .couponType(CouponType.GENERAL)
                .couponDiscountType(CouponDiscountType.PERCENT)
                .couponDiscountAmount(15)
                .couponMinimumOrderAmount(10000)
                .couponMaximumDiscountAmount(3000)
                .couponScope(CouponScope.ALL)
                .couponExpiredAt(LocalDateTime.now().plusDays(30))
                .couponIssuePeriod(null)
                .build();

        CouponPolicy savedPolicy = couponPolicyRepository.save(couponPolicy);

        CouponBook couponBook1 = new CouponBook(null, savedPolicy, 101L);
        CouponBook couponBook2 = new CouponBook(null, savedPolicy, 102L);

        couponBookRepository.saveAll(Arrays.asList(couponBook1, couponBook2));

        List<CouponBook> list = couponBookRepository.findByCouponPolicy_CouponId(savedPolicy.getCouponId());
        assertThat(list).hasSize(2);
        assertThat(list).extracting("bookId").containsExactlyInAnyOrder(101L, 102L);
    }

    @Test
    @DisplayName("existsByCouponPolicyIdAndBookIdsIn: 정상 동작 확인")
    void testExistsByCouponPolicyIdAndBookIdsIn() {
        CouponPolicy couponPolicy = CouponPolicy.builder()
                .couponName("Coupon For Exists Test")
                .couponType(CouponType.GENERAL)
                .couponDiscountType(CouponDiscountType.FIXED)
                .couponDiscountAmount(5000)
                .couponMinimumOrderAmount(50000)
                .couponMaximumDiscountAmount(5000)
                .couponScope(CouponScope.BOOK)
                .couponExpiredAt(LocalDateTime.now().plusDays(15))
                .couponIssuePeriod(null)
                .build();

        CouponPolicy savedPolicy = couponPolicyRepository.save(couponPolicy);

        CouponBook couponBookA = new CouponBook(null, savedPolicy, 201L);
        CouponBook couponBookB = new CouponBook(null, savedPolicy, 202L);
        couponBookRepository.saveAll(Arrays.asList(couponBookA, couponBookB));

        // 1) 존재하는 도서 ID 포함 시 true 기대
        boolean exists1 = couponBookRepository.existsByCouponPolicyIdAndBookIdsIn(savedPolicy.getCouponId(), List.of(201L, 999L));
        assertThat(exists1).isTrue();

        // 2) 존재하지 않는 도서 ID만 있을 시 false 기대
        boolean exists2 = couponBookRepository.existsByCouponPolicyIdAndBookIdsIn(savedPolicy.getCouponId(), List.of(999L, 888L));
        assertThat(exists2).isFalse();

        // 3) null 파라미터 전달 시 false 기대
        boolean existsNull = couponBookRepository.existsByCouponPolicyIdAndBookIdsIn(savedPolicy.getCouponId(), null);
        assertThat(existsNull).isFalse();

        // 4) 빈 리스트 전달 시 false 기대
        boolean existsEmpty = couponBookRepository.existsByCouponPolicyIdAndBookIdsIn(savedPolicy.getCouponId(), List.of());
        assertThat(existsEmpty).isFalse();
    }

    @Test
    @DisplayName("findBookIdsByCouponId 테스트")
    void testFindBookIdsByCouponId() {
        CouponPolicy policy = CouponPolicy.builder()
                .couponName("BookId Coupon")
                .couponType(CouponType.GENERAL)
                .couponDiscountType(CouponDiscountType.PERCENT)
                .couponDiscountAmount(10)
                .couponMinimumOrderAmount(20000)
                .couponMaximumDiscountAmount(3000)
                .couponScope(CouponScope.ALL)
                .couponExpiredAt(LocalDateTime.now().plusDays(60))
                .couponIssuePeriod(null)
                .build();

        CouponPolicy savedPolicy = couponPolicyRepository.save(policy);

        CouponBook couponBook1 = new CouponBook(null, savedPolicy, 301L);
        CouponBook couponBook2 = new CouponBook(null, savedPolicy, 302L);
        couponBookRepository.saveAll(List.of(couponBook1, couponBook2));

        List<Long> bookIds = couponBookRepository.findBookIdsByCouponId(savedPolicy.getCouponId());

        assertThat(bookIds).hasSize(2)
                .containsExactlyInAnyOrder(301L, 302L);
    }

    @Test
    @DisplayName("deleteByCouponPolicy 테스트")
    void testDeleteByCouponPolicy() {
        CouponPolicy policy = CouponPolicy.builder()
                .couponName("Delete Test Coupon")
                .couponType(CouponType.GENERAL)
                .couponDiscountType(CouponDiscountType.PERCENT)
                .couponDiscountAmount(5)
                .couponMinimumOrderAmount(1000)
                .couponMaximumDiscountAmount(500)
                .couponScope(CouponScope.ALL)
                .couponExpiredAt(LocalDateTime.now().plusDays(20))
                .couponIssuePeriod(null)
                .build();

        CouponPolicy savedPolicy = couponPolicyRepository.save(policy);

        CouponBook couponBook = new CouponBook(null, savedPolicy, 401L);
        couponBookRepository.save(couponBook);

        couponBookRepository.deleteByCouponPolicy(savedPolicy);

        List<CouponBook> list = couponBookRepository.findByCouponPolicy_CouponId(savedPolicy.getCouponId());
        assertThat(list).isEmpty();
    }
}
