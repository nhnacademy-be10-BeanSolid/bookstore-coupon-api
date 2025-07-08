package com.nhnacademy.controller.dto;

import com.nhnacademy.domain.CouponDiscountType;
import com.nhnacademy.domain.CouponPolicy;
import com.nhnacademy.domain.CouponScope;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class CouponPolicyDetailResponseTest {

    static Stream<Arguments> provideCouponPolicyData() {
        return Stream.of(
                arguments(
                        CouponPolicy.builder()
                                .couponId(1L)
                                .couponName("전체 할인 쿠폰")
                                .couponDiscountType(CouponDiscountType.PERCENT)
                                .couponDiscountAmount(15)
                                .couponMinimumOrderAmount(10000)
                                .couponMaximumDiscountAmount(5000)
                                .couponScope(CouponScope.ALL)
                                .couponExpiredAt(LocalDateTime.of(2025, 12, 31, 23, 59, 59))
                                .couponIssuePeriod(null)
                                .build(),
                        Collections.emptyList(),
                        Collections.emptyList()
                ),
                arguments(
                        CouponPolicy.builder()
                                .couponId(2L)
                                .couponName("도서 할인 쿠폰")
                                .couponDiscountType(CouponDiscountType.AMOUNT)
                                .couponDiscountAmount(2000)
                                .couponMinimumOrderAmount(null)
                                .couponMaximumDiscountAmount(null)
                                .couponScope(CouponScope.BOOK)
                                .couponExpiredAt(LocalDateTime.now().plusMonths(3))
                                .couponIssuePeriod(10)
                                .build(),
                        Arrays.asList(101L, 102L),
                        Collections.emptyList()
                ),
                arguments(
                        CouponPolicy.builder()
                                .couponId(3L)
                                .couponName("카테고리 할인 쿠폰")
                                .couponDiscountType(CouponDiscountType.PERCENT)
                                .couponDiscountAmount(5)
                                .couponMinimumOrderAmount(5000)
                                .couponMaximumDiscountAmount(1000)
                                .couponScope(CouponScope.CATEGORY)
                                .couponExpiredAt(LocalDateTime.now().plusMonths(1))
                                .couponIssuePeriod(null)
                                .build(),
                        Collections.emptyList(),
                        Arrays.asList(201L, 202L, 203L)
                ),
                arguments(
                        CouponPolicy.builder()
                                .couponId(4L)
                                .couponName("Null 필드 테스트")
                                .couponDiscountType(CouponDiscountType.AMOUNT)
                                .couponDiscountAmount(1000)
                                .couponMinimumOrderAmount(null)
                                .couponMaximumDiscountAmount(null)
                                .couponScope(CouponScope.ALL)
                                .couponExpiredAt(null)
                                .couponIssuePeriod(null)
                                .build(),
                        null,
                        null
                )
        );
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("provideCouponPolicyData")
    @DisplayName("CouponPolicyDetailResponse.from() 메소드 테스트 - 다양한 시나리오 매핑 확인")
    void from_mapsAllFieldsCorrectly(CouponPolicy policy, List<Long> bookIds, List<Long> categoryIds) {
        CouponPolicyDetailResponse response = CouponPolicyDetailResponse.from(policy, bookIds, categoryIds);

        assertThat(response).isNotNull();
        assertThat(response.getCouponId()).isEqualTo(policy.getCouponId());
        assertThat(response.getCouponName()).isEqualTo(policy.getCouponName());
        assertThat(response.getCouponDiscountType()).isEqualTo(policy.getCouponDiscountType());
        assertThat(response.getCouponDiscountAmount()).isEqualTo(policy.getCouponDiscountAmount());
        assertThat(response.getCouponMinimumOrderAmount()).isEqualTo(policy.getCouponMinimumOrderAmount());
        assertThat(response.getCouponMaximumDiscountAmount()).isEqualTo(policy.getCouponMaximumDiscountAmount());
        assertThat(response.getCouponScope()).isEqualTo(policy.getCouponScope());
        assertThat(response.getCouponExpiredAt()).isEqualTo(policy.getCouponExpiredAt());
        assertThat(response.getCouponIssuePeriod()).isEqualTo(policy.getCouponIssuePeriod());
        assertThat(response.getBookIds()).isEqualTo(bookIds);
        assertThat(response.getCategoryIds()).isEqualTo(categoryIds);
    }
}
