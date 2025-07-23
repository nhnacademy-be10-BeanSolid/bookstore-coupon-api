package com.nhnacademy.listener;

import com.nhnacademy.domain.CouponPolicy;
import com.nhnacademy.dto.request.IssueCouponsToUsersRequestDto;
import com.nhnacademy.exception.CouponNotFoundException;
import com.nhnacademy.repository.CouponPolicyRepository;
import com.nhnacademy.repository.UserCouponListRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponIssueListenerTest {

    @Mock
    private CouponPolicyRepository couponPolicyRepository;

    @Mock
    private UserCouponListRepository userCouponListRepository;

    @InjectMocks
    private CouponIssueListener listener;

    @Test
    void handleIssueCouponsToUsersRequest_success_withIssuePeriod() {

        Long couponPolicyId = 1L;
        List<Long> userNos = Arrays.asList(10L, 20L);
        IssueCouponsToUsersRequestDto request = new IssueCouponsToUsersRequestDto(couponPolicyId, userNos);

        CouponPolicy couponPolicy = mock(CouponPolicy.class);
        when(couponPolicy.getCouponIssuePeriod()).thenReturn(30);
        when(couponPolicyRepository.findById(couponPolicyId)).thenReturn(Optional.of(couponPolicy));

        listener.handleIssueCouponsToUsersRequest(request);

        verify(couponPolicyRepository, times(1)).findById(couponPolicyId);
        verify(userCouponListRepository, times(1)).saveAll(anyList());
    }

    @Test
    void handleIssueCouponsToUsersRequest_success_withFixedExpiredAt() {

        Long couponPolicyId = 1L;
        List<Long> userNos = Arrays.asList(11L, 22L, 33L);
        IssueCouponsToUsersRequestDto request = new IssueCouponsToUsersRequestDto(couponPolicyId, userNos);

        CouponPolicy couponPolicy = mock(CouponPolicy.class);
        when(couponPolicy.getCouponIssuePeriod()).thenReturn(null);
        when(couponPolicy.getCouponExpiredAt()).thenReturn(LocalDateTime.now().plusDays(5));
        when(couponPolicyRepository.findById(couponPolicyId)).thenReturn(Optional.of(couponPolicy));

        listener.handleIssueCouponsToUsersRequest(request);

        verify(userCouponListRepository, times(1)).saveAll(anyList());
    }

    @Test
    void handleIssueCouponsToUsersRequest_couponNotFound() {
        // given
        Long couponPolicyId = 99L;
        List<Long> userNos = List.of(101L);
        IssueCouponsToUsersRequestDto request = new IssueCouponsToUsersRequestDto(couponPolicyId, userNos);

        when(couponPolicyRepository.findById(couponPolicyId)).thenReturn(Optional.empty());

        try {
            listener.handleIssueCouponsToUsersRequest(request);
            assert false : "Should have thrown CouponNotFoundException";
        } catch (CouponNotFoundException e) {
            // 기대한 예외 발생 - 정상
        }

        verify(couponPolicyRepository, times(1)).findById(couponPolicyId);
        verify(userCouponListRepository, never()).saveAll(anyList());
    }
}

