package com.nhnacademy.controller.dto;

import com.nhnacademy.domain.CouponPolicy;
import com.nhnacademy.domain.UsedCoupon;
import com.nhnacademy.domain.UserCouponStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserCouponResponseTest {

    @Test
    @DisplayName("UserCouponResponse.from() 메소드 테스트 - UsedCoupon 엔티티로부터 올바른 매핑 확인")
    void from_mapsUsedCouponToResponse() {
        Long userCouponId = 1L;
        String userNo = "testUser1";
        Long couponPolicyId = 10L;
        String couponName = "테스트 쿠폰";
        LocalDateTime issuedAt = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime expiredAt = LocalDateTime.of(2024, 12, 31, 23, 59);
        LocalDateTime usedAt = LocalDateTime.of(2024, 2, 15, 11, 30);
        UserCouponStatus status = UserCouponStatus.USED;
        Long orderId = 12345L;

        CouponPolicy mockCouponPolicy = mock(CouponPolicy.class);
        when(mockCouponPolicy.getCouponId()).thenReturn(couponPolicyId);
        when(mockCouponPolicy.getCouponName()).thenReturn(couponName);

        UsedCoupon mockUsedCoupon = mock(UsedCoupon.class);
        when(mockUsedCoupon.getUserCouponId()).thenReturn(userCouponId);
        when(mockUsedCoupon.getUserNo()).thenReturn(userNo);
        when(mockUsedCoupon.getCouponPolicy()).thenReturn(mockCouponPolicy);
        when(mockUsedCoupon.getIssuedAt()).thenReturn(issuedAt);
        when(mockUsedCoupon.getExpiredAt()).thenReturn(expiredAt);
        when(mockUsedCoupon.getUsedAt()).thenReturn(usedAt);
        when(mockUsedCoupon.getStatus()).thenReturn(status);
        when(mockUsedCoupon.getOrderId()).thenReturn(orderId);

        UserCouponResponse response = UserCouponResponse.from(mockUsedCoupon);

        assertThat(response).isNotNull();
        assertThat(response.getUserCouponId()).isEqualTo(userCouponId);
        assertThat(response.getUserNo()).isEqualTo(userNo);
        assertThat(response.getCouponPolicyId()).isEqualTo(couponPolicyId);
        assertThat(response.getCouponName()).isEqualTo(couponName);
        assertThat(response.getIssuedAt()).isEqualTo(issuedAt);
        assertThat(response.getExpiredAt()).isEqualTo(expiredAt);
        assertThat(response.getUsedAt()).isEqualTo(usedAt);
        assertThat(response.getStatus()).isEqualTo(status);
        assertThat(response.getOrderId()).isEqualTo(orderId);
    }

    @Test
    @DisplayName("UserCouponResponse.from() 메소드 테스트 - 사용되지 않은 쿠폰 (usedAt, orderId null)")
    void from_mapsUnusedCouponToResponse() {
        Long userCouponId = 2L;
        String userNo = "testUser2";
        Long couponPolicyId = 20L;
        String couponName = "미사용 쿠폰";
        LocalDateTime issuedAt = LocalDateTime.of(2024, 3, 1, 9, 0);
        LocalDateTime expiredAt = LocalDateTime.of(2024, 9, 30, 23, 59);
        UserCouponStatus status = UserCouponStatus.ACTIVE;

        CouponPolicy mockCouponPolicy = mock(CouponPolicy.class);
        when(mockCouponPolicy.getCouponId()).thenReturn(couponPolicyId);
        when(mockCouponPolicy.getCouponName()).thenReturn(couponName);

        UsedCoupon mockUsedCoupon = mock(UsedCoupon.class);
        when(mockUsedCoupon.getUserCouponId()).thenReturn(userCouponId);
        when(mockUsedCoupon.getUserNo()).thenReturn(userNo);
        when(mockUsedCoupon.getCouponPolicy()).thenReturn(mockCouponPolicy);
        when(mockUsedCoupon.getIssuedAt()).thenReturn(issuedAt);
        when(mockUsedCoupon.getExpiredAt()).thenReturn(expiredAt);
        when(mockUsedCoupon.getUsedAt()).thenReturn(null);
        when(mockUsedCoupon.getStatus()).thenReturn(status);
        when(mockUsedCoupon.getOrderId()).thenReturn(null);

        UserCouponResponse response = UserCouponResponse.from(mockUsedCoupon);

        assertThat(response).isNotNull();
        assertThat(response.getUserCouponId()).isEqualTo(userCouponId);
        assertThat(response.getUserNo()).isEqualTo(userNo);
        assertThat(response.getCouponPolicyId()).isEqualTo(couponPolicyId);
        assertThat(response.getCouponName()).isEqualTo(couponName);
        assertThat(response.getIssuedAt()).isEqualTo(issuedAt);
        assertThat(response.getExpiredAt()).isEqualTo(expiredAt);
        assertThat(response.getUsedAt()).isNull();
        assertThat(response.getStatus()).isEqualTo(status);
        assertThat(response.getOrderId()).isNull();
    }

    @Test
    @DisplayName("UserCouponResponse: NoArgsConstructor, AllArgsConstructor, Builder 테스트")
    void testLombokConstructorsAndBuilder() {
        UserCouponResponse noArgResponse = new UserCouponResponse();
        assertThat(noArgResponse).isNotNull();

        Long userCouponId = 3L;
        String userNo = "testUser3";
        Long couponPolicyId = 30L;
        String couponName = "AllArgsConstructor 테스트";
        LocalDateTime issuedAt = LocalDateTime.now();
        LocalDateTime expiredAt = LocalDateTime.now().plusDays(10);
        LocalDateTime usedAt = null;
        UserCouponStatus status = UserCouponStatus.ACTIVE;
        Long orderId = null;

        UserCouponResponse allArgResponse = new UserCouponResponse(
                userCouponId, userNo, couponPolicyId, couponName, issuedAt, expiredAt, usedAt, status, orderId
        );
        assertThat(allArgResponse.getUserCouponId()).isEqualTo(userCouponId);
        assertThat(allArgResponse.getUserNo()).isEqualTo(userNo);
        assertThat(allArgResponse.getCouponPolicyId()).isEqualTo(couponPolicyId);
        assertThat(allArgResponse.getCouponName()).isEqualTo(couponName);
        assertThat(allArgResponse.getIssuedAt()).isEqualTo(issuedAt);
        assertThat(allArgResponse.getExpiredAt()).isEqualTo(expiredAt);
        assertThat(allArgResponse.getUsedAt()).isNull();
        assertThat(allArgResponse.getStatus()).isEqualTo(status);
        assertThat(allArgResponse.getOrderId()).isNull();

        UserCouponResponse builderResponse = UserCouponResponse.builder()
                .userCouponId(4L)
                .userNo("testUser4")
                .couponPolicyId(40L)
                .couponName("Builder 테스트")
                .issuedAt(LocalDateTime.now().minusDays(5))
                .expiredAt(LocalDateTime.now().plusDays(5))
                .status(UserCouponStatus.USED)
                .usedAt(LocalDateTime.now().minusDays(1))
                .orderId(54321L)
                .build();

        assertThat(builderResponse.getUserCouponId()).isEqualTo(4L);
        assertThat(builderResponse.getUserNo()).isEqualTo("testUser4");
        assertThat(builderResponse.getCouponPolicyId()).isEqualTo(40L);
        assertThat(builderResponse.getCouponName()).isEqualTo("Builder 테스트");
        assertThat(builderResponse.getIssuedAt()).isNotNull();
        assertThat(builderResponse.getExpiredAt()).isNotNull();
        assertThat(builderResponse.getUsedAt()).isNotNull();
        assertThat(builderResponse.getStatus()).isEqualTo(UserCouponStatus.USED);
        assertThat(builderResponse.getOrderId()).isEqualTo(54321L);
    }
}
