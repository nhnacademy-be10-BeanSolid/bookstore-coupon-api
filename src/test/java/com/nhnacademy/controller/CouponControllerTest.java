package com.nhnacademy.controller;

import com.nhnacademy.domain.CouponPolicy;
import com.nhnacademy.dto.request.CouponPolicyRequestDto;
import com.nhnacademy.dto.request.CouponUseRequestDto;
import com.nhnacademy.dto.request.IssueBookCouponRequestDto;
import com.nhnacademy.dto.response.CouponPolicyResponseDto;
import com.nhnacademy.domain.enumtype.CouponDiscountType;
import com.nhnacademy.domain.enumtype.CouponScope;
import com.nhnacademy.domain.enumtype.CouponType;
import com.nhnacademy.domain.UserCouponList;
import com.nhnacademy.service.CouponService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CouponController.class)
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CouponService couponService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("쿠폰 정책 생성 - 정상")
    void testCreateCouponPolicy() throws Exception {
        CouponPolicyRequestDto request = CouponPolicyRequestDto.builder()
                .couponName("Test Coupon")
                .couponDiscountType(CouponDiscountType.PERCENT)
                .couponDiscountAmount(10)
                .couponMinimumOrderAmount(10000)
                .couponMaximumDiscountAmount(5000)
                .couponScope(CouponScope.ALL)
                .couponIssuePeriod(30)
                .couponType(CouponType.GENERAL)
                .build();

        Mockito.when(couponService.createCouponPolicy(any())).thenReturn(null);

        mockMvc.perform(post("/coupons/policy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("쿠폰 정책 생성 - 유효성 검사 실패")
    void testCreateCouponPolicy_ValidationFailed() throws Exception {
        CouponPolicyRequestDto invalidRequest = CouponPolicyRequestDto.builder()
                .couponName("") // Invalid: empty name
                .couponDiscountType(CouponDiscountType.PERCENT)
                .couponDiscountAmount(10)
                .couponMinimumOrderAmount(10000)
                .couponMaximumDiscountAmount(5000)
                .couponScope(CouponScope.ALL)
                .couponIssuePeriod(30)
                .couponType(CouponType.GENERAL)
                .build();

        mockMvc.perform(post("/coupons/policy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("모든 쿠폰 정책 조회")
    void testGetAllCouponPolicies() throws Exception {
        CouponPolicyResponseDto dto1 = CouponPolicyResponseDto.builder()
                .couponId(1L).couponName("Coupon1").build();
        CouponPolicyResponseDto dto2 = CouponPolicyResponseDto.builder()
                .couponId(2L).couponName("Coupon2").build();

        Mockito.when(couponService.getAllCouponPolicies())
                .thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/coupons/policy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].couponId").value(1))
                .andExpect(jsonPath("$[1].couponName").value("Coupon2"));
    }

    @Test
    @DisplayName("단일 쿠폰 정책 조회")
    void testGetCouponPolicy() throws Exception {
        CouponPolicy couponPolicy = CouponPolicy.builder()
                .couponId(1L)
                .couponName("Found Coupon")
                .couponDiscountType(CouponDiscountType.PERCENT)
                .couponDiscountAmount(10)
                .couponMinimumOrderAmount(10000)
                .couponMaximumDiscountAmount(3000)
                .couponScope(CouponScope.ALL)
                .couponExpiredAt(LocalDateTime.now().plusDays(10))
                .couponIssuePeriod(30)
                .couponType(CouponType.GENERAL)
                .build();

        Mockito.when(couponService.getCouponPolicy(1L)).thenReturn(couponPolicy);

        mockMvc.perform(get("/coupons/policy/{policyId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.couponId").value(1))
                .andExpect(jsonPath("$.couponName").value("Found Coupon"));
    }

    @Test
    @DisplayName("유저에게 쿠폰 발급")
    void testIssueCouponToUser() throws Exception {
        Long userNo= 100L;
        Long couponPolicyId = 1L;

        UserCouponList issuedCoupon = UserCouponList.builder()
                .userNo(userNo)
                .build();

        Mockito.when(couponService.issueCouponToUser(userNo, couponPolicyId)).thenReturn(issuedCoupon);

        mockMvc.perform(post("/coupons/users/{userNo}/issue/{couponPolicyId}", userNo, couponPolicyId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userNo").value(userNo));
    }

    @Test
    @DisplayName("웰컴 쿠폰 발급")
    void testIssueWelcomeCoupon() throws Exception {
        Long userNo = 200L;
        UserCouponList welcomeCoupon = UserCouponList.builder()
                .userNo(userNo)
                .build();

        Mockito.when(couponService.issueWelcomeCoupon(userNo)).thenReturn(welcomeCoupon);

        mockMvc.perform(post("/coupons/users/{userNo}/issue-welcome", userNo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userNo").value(userNo));
    }

    @Test
    @DisplayName("생일 쿠폰 발급")
    void testIssueBirthdayCoupon() throws Exception {
        Long userNo = 300L;
        int birthMonth = 5;
        LocalDate userBirthDate = LocalDate.now().withMonth(birthMonth).withDayOfMonth(1);

        UserCouponList birthdayCoupon = UserCouponList.builder()
                .userNo(userNo)
                .build();

        Mockito.when(couponService.issueBirthdayCoupon(userNo, userBirthDate)).thenReturn(birthdayCoupon);

        mockMvc.perform(post("/coupons/users/{userNo}/issue-birthday", userNo)
                        .param("birthMonth", String.valueOf(birthMonth)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userNo").value(userNo));
    }

    @Test
    @DisplayName("도서 쿠폰 발급")
    void testIssueBookCoupon() throws Exception {
        IssueBookCouponRequestDto requestDto = IssueBookCouponRequestDto.builder()
                .userId(10L)
                .bookId(20L)
                .couponPolicyId(1L)
                .build();

        UserCouponList issuedCoupon = UserCouponList.builder()
                .userNo(10L)
                .build();

        Mockito.when(couponService.issueBookCoupon(any())).thenReturn(issuedCoupon);

        mockMvc.perform(post("/coupons/issue/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userNo").value(10));
    }

    @Test
    @DisplayName("유저 활성 쿠폰 목록 조회")
    void testGetActiveUserCoupons() throws Exception {
        Long userNo= 1000L;
        UserCouponList coupon1 = UserCouponList.builder().userNo(userNo).build();
        UserCouponList coupon2 = UserCouponList.builder().userNo(userNo).build();

        Mockito.when(couponService.getActiveUserCoupons(userNo)).thenReturn(List.of(coupon1, coupon2));

        mockMvc.perform(get("/coupons/users/{userNo}/active", userNo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("유저 사용 쿠폰 목록 조회")
    void testGetUsedUserCoupons() throws Exception {
        Long userNo= 2000L;
        UserCouponList coupon1 = UserCouponList.builder().userNo(userNo).build();

        Mockito.when(couponService.getUsedUserCoupons(userNo)).thenReturn(List.of(coupon1));

        mockMvc.perform(get("/coupons/users/{userNo}/used", userNo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("쿠폰 사용")
    void testUseCoupon() throws Exception {
        Long userNo = 55L;
        Long userCouponId = 100L;
        Long orderId = 1L;

        CouponUseRequestDto requestDto = CouponUseRequestDto.builder()
                .orderId(orderId)
                .build();


        mockMvc.perform(post("/coupons/users/{userNo}/use/{userCouponId}", userNo, userCouponId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("쿠폰이 성공적으로 사용되었습니다."));

        Mockito.verify(couponService).useCoupon(userNo, userCouponId, orderId);
    }

    @Test
    @DisplayName("주문 할인금액 계산")
    void testCalculateDiscount() throws Exception {
        Long userNo = 11L;
        Long userCouponId = 5L;
        int orderAmount = 20000;

        Mockito.when(couponService.calculateDiscountAmount(eq(userNo), eq(userCouponId), eq(orderAmount), anyList(), anyList()))
                .thenReturn(3000);

        mockMvc.perform(get("/coupons/users/{userNo}/calculate-discount/{userCouponId}", userNo, userCouponId)
                        .param("orderAmount", String.valueOf(orderAmount))
                        .param("bookIds", "3", "4")
                        .param("categoryIds", "7"))
                .andExpect(status().isOk())
                .andExpect(content().string("3000"));
    }
}

