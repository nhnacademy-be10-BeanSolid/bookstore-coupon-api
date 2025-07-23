package com.nhnacademy.listener;

import com.nhnacademy.event.UserRegisteredEvent;
import com.nhnacademy.service.CouponService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRegisteredEventListenerTest {

    @Mock
    private CouponService couponService;

    @InjectMocks
    private UserRegisteredEventListener listener;

    @Test
    void handleUserRegisteredEvent_success() {
        Long userNo = 99L;
        UserRegisteredEvent event = mock(UserRegisteredEvent.class);
        when(event.getUserNo()).thenReturn(userNo);

        listener.handleUserRegisteredEvent(event);

        verify(couponService, times(1)).issueWelcomeCoupon(userNo);
    }

    @Test
    void handleUserRegisteredEvent_exception() {

        Long userNo = 77L;
        UserRegisteredEvent event = mock(UserRegisteredEvent.class);
        when(event.getUserNo()).thenReturn(userNo);

        doThrow(new RuntimeException("DB error"))
                .when(couponService)
                .issueWelcomeCoupon(userNo);

        listener.handleUserRegisteredEvent(event);

        verify(couponService, times(1)).issueWelcomeCoupon(userNo);
    }
}
