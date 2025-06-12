package com.nhnacademy.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@DisplayName("쿠폰 관련 예외 클래스 테스트")
public class CouponExceptionTests {

    @Nested
    @DisplayName("CouponAlreadyUsedException 테스트")
    public class CouponAlreadyUsedExceptionTest {
        @Test
        @DisplayName("생성자 호출 및 메시지 확인")
        public void testCreationAndMessage() {
            String testMessage = "이 쿠폰은 이미 사용되었습니다.";
            CouponAlreadyUsedException exception = new CouponAlreadyUsedException(testMessage);
            assertNotNull(exception);
            assertEquals(testMessage, exception.getMessage());
        }

        @Test
        @DisplayName("@ResponseStatus 확인 - BAD_REQUEST")
        public void testResponseStatus() {
            ResponseStatus responseStatus = CouponAlreadyUsedException.class.getAnnotation(ResponseStatus.class);
            assertNotNull(responseStatus);
            assertEquals(HttpStatus.BAD_REQUEST, responseStatus.value());
        }
    }

    @Nested
    @DisplayName("CouponExpiredException 테스트")
    public class CouponExpiredExceptionTest {
        @Test
        @DisplayName("생성자 호출 및 메시지 확인")
        public void testCreationAndMessage() {
            String testMessage = "쿠폰 사용 기간이 만료되었습니다.";
            CouponExpiredException exception = new CouponExpiredException(testMessage);
            assertNotNull(exception);
            assertEquals(testMessage, exception.getMessage());
        }

        @Test
        @DisplayName("@ResponseStatus 확인 - BAD_REQUEST")
        public void testResponseStatus() {
            ResponseStatus responseStatus = CouponExpiredException.class.getAnnotation(ResponseStatus.class);
            assertNotNull(responseStatus);
            assertEquals(HttpStatus.BAD_REQUEST, responseStatus.value());
        }
    }

    @Nested
    @DisplayName("CouponNotApplicableException 테스트")
    public class CouponNotApplicableExceptionTest {
        @Test
        @DisplayName("생성자 호출 및 메시지 확인")
        public void testCreationAndMessage() {
            String testMessage = "이 쿠폰은 현재 주문에 적용할 수 없습니다.";
            CouponNotApplicableException exception = new CouponNotApplicableException(testMessage);
            assertNotNull(exception);
            assertEquals(testMessage, exception.getMessage());
        }

        @Test
        @DisplayName("@ResponseStatus 확인 - BAD_REQUEST")
        public void testResponseStatus() {
            ResponseStatus responseStatus = CouponNotApplicableException.class.getAnnotation(ResponseStatus.class);
            assertNotNull(responseStatus);
            assertEquals(HttpStatus.BAD_REQUEST, responseStatus.value());
        }
    }

    @Nested
    @DisplayName("CouponNotFoundException 테스트")
    public class CouponNotFoundExceptionTest {
        @Test
        @DisplayName("생성자 호출 및 메시지 확인")
        public void testCreationAndMessage() {
            String testMessage = "해당 쿠폰 정책을 찾을 수 없습니다.";
            CouponNotFoundException exception = new CouponNotFoundException(testMessage);
            assertNotNull(exception);
            assertEquals(testMessage, exception.getMessage());
        }

        @Test
        @DisplayName("@ResponseStatus 확인 - NOT_FOUND")
        public void testResponseStatus() {
            ResponseStatus responseStatus = CouponNotFoundException.class.getAnnotation(ResponseStatus.class);
            assertNotNull(responseStatus);
            assertEquals(HttpStatus.NOT_FOUND, responseStatus.value());
        }
    }

    @Nested
    @DisplayName("UserCouponNotFoundException 테스트")
        public class UserCouponNotFoundExceptionTest {
        @Test
        @DisplayName("생성자 호출 및 메시지 확인")
        public void testCreationAndMessage() {
            String testMessage = "사용자에게 해당 쿠폰이 발급되지 않았습니다.";
            UserCouponNotFoundException exception = new UserCouponNotFoundException(testMessage);
            assertNotNull(exception);
            assertEquals(testMessage, exception.getMessage());
        }

        @Test
        @DisplayName("@ResponseStatus 확인 - NOT_FOUND")
        public void testResponseStatus() {
            ResponseStatus responseStatus = UserCouponNotFoundException.class.getAnnotation(ResponseStatus.class);
            assertNotNull(responseStatus);
            assertEquals(HttpStatus.NOT_FOUND, responseStatus.value());
        }
    }

    @Nested
    @DisplayName("WelcomeCouponPolicyNotFoundException 테스트")
    public class WelcomeCouponPolicyNotFoundExceptionTest {
        @Test
        @DisplayName("생성자 호출 및 메시지 확인")
        public void testCreationAndMessage() {
            String testMessage = "Welcome 쿠폰 정책을 찾을 수 없습니다.";
            WelcomeCouponPolicyNotFoundException exception = new WelcomeCouponPolicyNotFoundException(testMessage);
            assertNotNull(exception);
            assertEquals(testMessage, exception.getMessage());
        }

        @Test
        @DisplayName("@ResponseStatus 확인 - NOT_FOUND")
        public void testResponseStatus() {
            ResponseStatus responseStatus = WelcomeCouponPolicyNotFoundException.class.getAnnotation(ResponseStatus.class);
            assertNotNull(responseStatus);
            assertEquals(HttpStatus.NOT_FOUND, responseStatus.value());
        }
    }
}