package com.nhnacademy.controller.dto;

import com.nhnacademy.domain.CouponDiscountType;
import com.nhnacademy.domain.CouponScope;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class CouponPolicyRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    static Stream<Arguments> provideInvalidCouponPolicyRequests() {
        return Stream.of(
                arguments(
                        new CouponPolicyRequest(
                                "정상 쿠폰 정책", CouponDiscountType.AMOUNT, 1000,
                                5000, null, CouponScope.ALL, LocalDateTime.now().plusDays(30), 7,
                                Collections.emptyList(), Collections.emptyList()
                        ),
                        0,
                        Collections.emptyList()
                ),
                arguments(
                        new CouponPolicyRequest(
                                null, CouponDiscountType.AMOUNT, 1000,
                                5000, null, CouponScope.ALL, LocalDateTime.now().plusDays(30), 7,
                                Collections.emptyList(), Collections.emptyList()
                        ),
                        1,
                        List.of("couponName")
                ),
                arguments(
                        new CouponPolicyRequest(
                                "", CouponDiscountType.AMOUNT, 1000,
                                5000, null, CouponScope.ALL, LocalDateTime.now().plusDays(30), 7,
                                Collections.emptyList(), Collections.emptyList()
                        ),
                        1,
                        List.of("couponName")
                ),
                arguments(
                        new CouponPolicyRequest(
                                "   ", CouponDiscountType.AMOUNT, 1000,
                                5000, null, CouponScope.ALL, LocalDateTime.now().plusDays(30), 7,
                                Collections.emptyList(), Collections.emptyList()
                        ),
                        1,
                        List.of("couponName")
                ),
                arguments(
                        new CouponPolicyRequest(
                                "할인 유형 없음", null, 1000,
                                5000, null, CouponScope.ALL, LocalDateTime.now().plusDays(30), 7,
                                Collections.emptyList(), Collections.emptyList()
                        ),
                        1,
                        List.of("couponDiscountType")
                ),
                arguments(
                        new CouponPolicyRequest(
                                "음수 할인 금액", CouponDiscountType.AMOUNT, -100,
                                5000, null, CouponScope.ALL, LocalDateTime.now().plusDays(30), 7,
                                Collections.emptyList(), Collections.emptyList()
                        ),
                        1,
                        List.of("couponDiscountAmount")
                ),
                arguments(
                        new CouponPolicyRequest(
                                "음수 최소 주문 금액", CouponDiscountType.AMOUNT, 1000,
                                -5000, null, CouponScope.ALL, LocalDateTime.now().plusDays(30), 7,
                                Collections.emptyList(), Collections.emptyList()
                        ),
                        1,
                        List.of("couponMinimumOrderAmount")
                ),
                arguments(
                        new CouponPolicyRequest(
                                "음수 최대 할인 금액", CouponDiscountType.PERCENT, 10,
                                10000, -1000, CouponScope.ALL, LocalDateTime.now().plusDays(30), 7,
                                Collections.emptyList(), Collections.emptyList()
                        ),
                        1,
                        List.of("couponMaximumDiscountAmount")
                ),
                arguments(
                        new CouponPolicyRequest(
                                "범위 없음", CouponDiscountType.AMOUNT, 1000,
                                5000, null, null, LocalDateTime.now().plusDays(30), 7,
                                Collections.emptyList(), Collections.emptyList()
                        ),
                        1,
                        List.of("couponScope")
                ),
                arguments(
                        new CouponPolicyRequest(
                                "",
                                null,
                                -500,
                                -100,
                                -200,
                                null,
                                null, null, null, null
                        ),
                        6,
                        List.of(
                                "couponName",
                                "couponDiscountType",
                                "couponDiscountAmount",
                                "couponMinimumOrderAmount",
                                "couponMaximumDiscountAmount",
                                "couponScope"
                        )
                )
        );
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("provideInvalidCouponPolicyRequests")
    @DisplayName("CouponPolicyRequest 유효성 검사 - 다양한 시나리오")
    void validateCouponPolicyRequest(CouponPolicyRequest request, int expectedViolationCount, List<String> expectedViolationPaths) {
        Set<ConstraintViolation<CouponPolicyRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(expectedViolationCount);
        if (expectedViolationCount > 0) {
            assertThat(violations)
                    .extracting("propertyPath")
                    .map(Object::toString)
                    .containsExactlyInAnyOrderElementsOf(expectedViolationPaths);
        } else {
            assertThat(violations).isEmpty();
        }
    }

    @Test
    void getterCoverageTest() {
        CouponPolicyRequest req = new CouponPolicyRequest(
                "테스트", CouponDiscountType.AMOUNT, 1000, 2000, 3000, CouponScope.ALL,
                LocalDateTime.now(), 7, List.of(1L), List.of(2L)
        );
        req.getCouponName();
        req.getCouponDiscountType();
        req.getCouponDiscountAmount();
        req.getCouponMinimumOrderAmount();
        req.getCouponMaximumDiscountAmount();
        req.getCouponScope();
        req.getCouponExpiredAt();
        req.getCouponIssuePeriod();
        req.getBookIds();
        req.getCategoryIds();
    }
}
