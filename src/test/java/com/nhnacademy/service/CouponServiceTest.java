package com.nhnacademy.service;

import com.nhnacademy.domain.*;
import com.nhnacademy.domain.enumtype.CouponDiscountType;
import com.nhnacademy.domain.enumtype.CouponScope;
import com.nhnacademy.domain.enumtype.CouponType;
import com.nhnacademy.domain.enumtype.UserCouponStatus;
import com.nhnacademy.dto.request.CouponPolicyRequest;
import com.nhnacademy.exception.CouponNotFoundException;
import com.nhnacademy.repository.CouponPolicyRepository;
import com.nhnacademy.repository.UserCouponRepository;
import com.nhnacademy.service.impl.CouponServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class CouponServiceTest {

    @Mock
    private CouponPolicyRepository couponPolicyRepository;

    @Mock
    private UserCouponRepository userCouponRepository;

    @InjectMocks
    private CouponServiceImpl couponService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("쿠폰 정책 생성")
    void testCreateCouponPolicy() {
        when(couponPolicyRepository.save(any(CouponPolicy.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CouponPolicyRequest couponPolicyRequest = new CouponPolicyRequest(
                "Welcome Coupon",
                CouponDiscountType.PERCENT,
                10,
                20000,
                5000,
                CouponScope.ALL,
                null,
                365,
                CouponType.WELCOME,
                null,
                null
        );

        CouponPolicy result = couponService.createCouponPolicy(couponPolicyRequest);

        assertThat(result).isNotNull();
        assertThat(result.getCouponName()).isEqualTo("Welcome Coupon");
        verify(couponPolicyRepository, times(1)).save(any(CouponPolicy.class));
    }

    @Test
    @DisplayName("사용자에게 쿠폰 발급")
    void testIssueCouponToUser() {
        CouponPolicy policy = CouponPolicy.builder()
                .couponId(1L)
                .couponIssuePeriod(30)
                .build();
        when(couponPolicyRepository.findById(anyLong())).thenReturn(Optional.of(policy));
        when(userCouponRepository.save(any(UserCoupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserCoupon result = couponService.issueCouponToUser(100L, 1L);

        assertThat(result).isNotNull();
        assertThat(result.getUserNo()).isEqualTo(100L);
        assertThat(result.getStatus()).isEqualTo(UserCouponStatus.ACTIVE);
        verify(couponPolicyRepository, times(1)).findById(1L);
        verify(userCouponRepository, times(1)).save(any(UserCoupon.class));
    }

    @Test
    @DisplayName("존재하지 않는 쿠폰 정책으로 발급 시 예외 발생")
    void testIssueCouponToUser_policyNotFound() {

        when(couponPolicyRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(CouponNotFoundException.class, () -> {
            couponService.issueCouponToUser(100L, 1L);
        });
    }
}