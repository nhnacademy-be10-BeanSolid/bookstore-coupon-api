package com.nhnacademy.controller.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CouponUseRequestTest {

    @Test
    @DisplayName("CouponUseRequest: NoArgsConstructor 및 Setter 테스트")
    void testNoArgsConstructorAndSetter() {
        CouponUseRequest request = new CouponUseRequest();
        Long orderId = 12345L;
        request.setOrderId(orderId);

        assertThat(request.getOrderId()).isEqualTo(orderId);
    }

    @Test
    @DisplayName("CouponUseRequest: AllArgsConstructor 및 Getter 테스트")
    void testAllArgsConstructorAndGetter() {
        Long orderId = 67890L;
        CouponUseRequest request = new CouponUseRequest(orderId);

        assertThat(request.getOrderId()).isEqualTo(orderId);
    }
}
