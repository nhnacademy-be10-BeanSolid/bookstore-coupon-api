package com.nhnacademy.controller;

import com.nhnacademy.service.CouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CouponAdminControllerTest {

    @Mock
    private CouponService couponService;

    @InjectMocks
    private CouponAdminController couponAdminController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(couponAdminController).build();
    }

    @Test
    @DisplayName("관리자: 모든 사용자에게 쿠폰 발급 시작")
    void testStartIssuingCouponsToAllUsers() throws Exception {
        Long couponPolicyId = 1L;

        mockMvc.perform(post("/coupons/admin/issue-all/{couponPolicyId}", couponPolicyId))
                .andExpect(status().isAccepted());

        verify(couponService).startCouponIssuingProcess(couponPolicyId);
    }

    @Test
    @DisplayName("관리자: 특정 도서에 쿠폰 적용")
    void testIssueCouponToBook() throws Exception {
        Long couponPolicyId = 1L;
        Long bookId = 101L;

        mockMvc.perform(post("/coupons/admin/issue-book/{couponPolicyId}/{bookId}", couponPolicyId, bookId))
                .andExpect(status().isAccepted());

        verify(couponService).issueCouponToBook(couponPolicyId, bookId);
    }
}