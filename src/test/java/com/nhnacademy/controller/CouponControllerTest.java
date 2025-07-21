package com.nhnacademy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nhnacademy.controller.dto.CouponPolicyRequest;
import com.nhnacademy.controller.dto.CouponUseRequest;
import com.nhnacademy.domain.*;
import com.nhnacademy.service.CouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CouponControllerTest {

    @Mock
    private CouponService couponService;

    @InjectMocks
    private CouponController couponController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(couponController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    private CouponPolicy createMockPolicy(Long id, String name) {
        return CouponPolicy.builder()
                .couponId(id)
                .couponName(name)
                .couponDiscountAmount(1000) // Default value for DTO conversion
                .build();
    }

    @Test
    @DisplayName("쿠폰 정책 생성 API - 성공")
    void testCreateCouponPolicy() throws Exception {
        CouponPolicyRequest request = new CouponPolicyRequest(
                "New Policy", CouponDiscountType.AMOUNT, 5000, 30000, 5000,
                CouponScope.ALL, LocalDateTime.now().plusDays(10), 30, CouponType.GENERAL, null, null);
        
        CouponPolicy returnedPolicy = createMockPolicy(1L, "New Policy");

        when(couponService.createCouponPolicy(
                anyString(), any(CouponDiscountType.class), anyInt(), any(Integer.class), any(Integer.class), 
                any(CouponScope.class), any(LocalDateTime.class), any(Integer.class), nullable(List.class), 
                nullable(List.class), any(CouponType.class))
        ).thenReturn(returnedPolicy);

        mockMvc.perform(post("/coupons/policy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.couponId").value(1L));
    }

    @Test
    @DisplayName("사용자에게 일반 쿠폰 발급 API")
    void testIssueCouponToUser() throws Exception {
        CouponPolicy policy = createMockPolicy(1L, "General Coupon");
        UsedCoupon usedCoupon = UsedCoupon.builder().userCouponId(1L).couponPolicy(policy).build();
        when(couponService.issueCouponToUser(100L, 1L)).thenReturn(usedCoupon);

        mockMvc.perform(post("/coupons/users/100/issue/1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.couponName").value("General Coupon"));
    }

    @Test
    @DisplayName("신규 가입자에게 웰컴 쿠폰 발급 API")
    void testIssueWelcomeCoupon() throws Exception {
        CouponPolicy policy = createMockPolicy(2L, "Welcome Coupon");
        UsedCoupon welcomeCoupon = UsedCoupon.builder().userCouponId(2L).couponPolicy(policy).build();
        when(couponService.issueWelcomeCoupon(100L)).thenReturn(welcomeCoupon);

        mockMvc.perform(post("/coupons/users/100/issue-welcome"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.couponName").value("Welcome Coupon"));
    }

    @Test
    @DisplayName("생일인 사용자에게 생일 쿠폰 발급 API")
    void testIssueBirthdayCoupon() throws Exception {
        CouponPolicy policy = createMockPolicy(3L, "Birthday Coupon");
        UsedCoupon birthdayCoupon = UsedCoupon.builder().userCouponId(3L).couponPolicy(policy).build();
        when(couponService.issueBirthdayCoupon(eq(100L), any(LocalDate.class))).thenReturn(birthdayCoupon);

        mockMvc.perform(post("/coupons/users/100/issue-birthday").param("birthMonth", "7"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.couponName").value("Birthday Coupon"));
    }

    @Test
    @DisplayName("사용자의 활성 쿠폰 목록 조회 API")
    void testGetActiveUserCoupons() throws Exception {
        CouponPolicy policy = createMockPolicy(1L, "Active Coupon");
        UsedCoupon activeCoupon = UsedCoupon.builder().userCouponId(1L).couponPolicy(policy).status(UserCouponStatus.ACTIVE).build();
        List<UsedCoupon> coupons = Collections.singletonList(activeCoupon);
        when(couponService.getActiveUserCoupons(100L)).thenReturn(coupons);

        mockMvc.perform(get("/coupons/users/100/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].couponName").value("Active Coupon"));
    }

    @Test
    @DisplayName("쿠폰 사용 처리 API")
    void testUseCoupon() throws Exception {
        CouponUseRequest request = new CouponUseRequest(999L);
        mockMvc.perform(post("/coupons/users/100/use/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}