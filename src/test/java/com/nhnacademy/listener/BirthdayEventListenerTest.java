package com.nhnacademy.listener;

import com.nhnacademy.event.UserBirthEvent;
import com.nhnacademy.service.CouponService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BirthdayEventListenerTest {

    @Mock
    private CouponService couponService;

    @InjectMocks
    private BirthdayEventListener listener;

    @Test
    void handleBirthdayEvent_success() {
        // given
        Long userNo = 1L;
        LocalDate userBirth = LocalDate.of(1970, 1, 1);
        UserBirthEvent event = mock(UserBirthEvent.class);
        when(event.getUserNo()).thenReturn(userNo);
        when(event.getUserBirth()).thenReturn(userBirth);

        // when
        listener.handleBirthdayEvent(event);

        // then
        verify(couponService, times(1)).issueBirthdayCoupon(userNo, userBirth);
    }

    @Test
    void handleBirthdayEvent_couponServiceThrowsException() {
        // given
        Long userNo = 2L;
        LocalDate userBirth = LocalDate.of(1995, 5, 15);
        UserBirthEvent event = mock(UserBirthEvent.class);
        when(event.getUserNo()).thenReturn(userNo);
        when(event.getUserBirth()).thenReturn(userBirth);

        doThrow(new RuntimeException("Test exception"))
                .when(couponService)
                .issueBirthdayCoupon(userNo, userBirth);

        // when
        listener.handleBirthdayEvent(event);

        // then
        verify(couponService, times(1)).issueBirthdayCoupon(userNo, userBirth);
        // 로그 및 예외 처리 검증은 필요 시 추가
    }
}
