package com.nhnacademy.listener;

import com.nhnacademy.common.exception.CouponAlreadyExistException;
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
class UserBirthEventListenerTest {

    @Mock
    private CouponService couponService;

    @InjectMocks
    private UserBirthEventListener listener;

    @Test
    void handleUserBirthEvent_success() {

        Long userNo = 123L;
        LocalDate userBrith = LocalDate.of(1990, 8, 12);
        UserBirthEvent event = mock(UserBirthEvent.class);
        when(event.getUserNo()).thenReturn(userNo);
        when(event.getUserBirth()).thenReturn(userBrith);

        listener.handleUserBirthEvent(event);

        verify(couponService, times(1)).issueBirthdayCoupon(userNo, userBrith);
    }

    @Test
    void handleUserBirthEvent_alreadyExistException() {
        // given
        Long userNo = 456L;
        LocalDate userBrith = LocalDate.of(1985, 1, 1);
        UserBirthEvent event = mock(UserBirthEvent.class);
        when(event.getUserNo()).thenReturn(userNo);
        when(event.getUserBirth()).thenReturn(userBrith);

        doThrow(new CouponAlreadyExistException("Already issued"))
                .when(couponService)
                .issueBirthdayCoupon(userNo, userBrith);

        listener.handleUserBirthEvent(event);

        verify(couponService, times(1)).issueBirthdayCoupon(userNo, userBrith);
    }

    @Test
    void handleUserBirthEvent_otherException() {
        // given
        Long userNo = 789L;
        LocalDate userBrith = LocalDate.of(1970, 7, 7);
        UserBirthEvent event = mock(UserBirthEvent.class);
        when(event.getUserNo()).thenReturn(userNo);
        when(event.getUserBirth()).thenReturn(userBrith);

        doThrow(new RuntimeException("DB error!"))
                .when(couponService)
                .issueBirthdayCoupon(userNo, userBrith);

        listener.handleUserBirthEvent(event);

        verify(couponService, times(1)).issueBirthdayCoupon(userNo, userBrith);
    }
}

