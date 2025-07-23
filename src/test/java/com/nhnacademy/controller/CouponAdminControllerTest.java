package com.nhnacademy.controller;

import com.nhnacademy.common.exception.ValidationFailedException;
import com.nhnacademy.domain.CouponPolicy;
import com.nhnacademy.dto.request.CouponPolicyRequestDto;
import com.nhnacademy.dto.response.CouponPolicyResponseDto;
import com.nhnacademy.domain.enumtype.CouponDiscountType;
import com.nhnacademy.domain.enumtype.CouponScope;
import com.nhnacademy.domain.enumtype.CouponType;
import com.nhnacademy.service.CouponService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CouponAdminController.class)
class CouponAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CouponService couponService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("관리자: 모든 사용자에게 쿠폰 발급 시작")
    void testStartIssuingCouponsToAllUsers() throws Exception {
        Long couponPolicyId = 1L;

        mockMvc.perform(post("/admin/issue-all/{couponPolicyId}", couponPolicyId))
                .andExpect(status().isAccepted());

        Mockito.verify(couponService).startCouponIssuingProcess(couponPolicyId);
    }

    @Test
    @DisplayName("관리자: 특정 도서에 쿠폰 적용")
    void testIssueCouponToBook() throws Exception {
        Long couponPolicyId = 1L;
        Long bookId = 101L;

        mockMvc.perform(post("/admin/issue-book")
                        .param("couponPolicyId", String.valueOf(couponPolicyId))
                        .param("bookId", String.valueOf(bookId)))
                .andExpect(status().isAccepted());

        Mockito.verify(couponService).issueCouponToBook(couponPolicyId, bookId);
    }

    @Test
    @DisplayName("관리자: 특정 유저에게 쿠폰 발급")
    void testIssueCouponToUser() throws Exception {
        Long userNo = 1L;
        Long couponPolicyId = 1L;

        mockMvc.perform(post("/admin/issue-to-user")
                        .param("userNo", String.valueOf(userNo))
                        .param("couponPolicyId", String.valueOf(couponPolicyId)))
                .andExpect(status().isAccepted());

        Mockito.verify(couponService).issueCouponToUser(userNo, couponPolicyId);
    }

    @Test
    @DisplayName("관리자: 쿠폰 정책 생성 - 정상")
    void testCreateCouponPolicy_Success() throws Exception {
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

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/admin/coupon-policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        Mockito.verify(couponService).createCouponPolicy(any(CouponPolicyRequestDto.class));
    }

    @Test
    @DisplayName("관리자: 쿠폰 정책 생성 - 유효성 예외 발생")
    void testCreateCouponPolicy_ValidationFailed() throws Exception {
        CouponPolicyRequestDto invalidRequest = CouponPolicyRequestDto.builder()
                .couponName("")
                .build();

        BDDMockito.willThrow(new ValidationFailedException(new BindException(invalidRequest, "couponPolicyRequestDto")))
                .given(couponService).createCouponPolicy(any());

        String json = objectMapper.writeValueAsString(invalidRequest);

        mockMvc.perform(post("/admin/coupon-policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("관리자: 모든 쿠폰 정책 조회")
    void testGetAllCouponPolicies() throws Exception {
        CouponPolicyResponseDto dto1 = CouponPolicyResponseDto.builder()
                .couponId(1L)
                .couponName("Coupon1")
                .build();
        CouponPolicyResponseDto dto2 = CouponPolicyResponseDto.builder()
                .couponId(2L)
                .couponName("Coupon2")
                .build();

        List<CouponPolicyResponseDto> list = Arrays.asList(dto1, dto2);
        Mockito.when(couponService.getAllCouponPolicies()).thenReturn(list);

        mockMvc.perform(get("/admin/coupon-policies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(list.size()))
                .andExpect(jsonPath("$[0].couponId").value(1))
                .andExpect(jsonPath("$[1].couponName").value("Coupon2"));
    }

    @Test
    @DisplayName("관리자: 단일 쿠폰 정책 조회 - 존재")
    void testGetCouponPolicyById_Found() throws Exception {

        CouponPolicy couponPolicy = CouponPolicy.builder()
                .couponId(1L)
                .couponName("Found Coupon")
                .couponDiscountType(CouponDiscountType.PERCENT)
                .couponDiscountAmount(10)
                .couponMinimumOrderAmount(10000)
                .couponMaximumDiscountAmount(5000)
                .couponScope(CouponScope.ALL)
                .couponExpiredAt(LocalDateTime.now().plusDays(10))
                .couponIssuePeriod(30)
                .couponType(CouponType.GENERAL)
                .build();

        Mockito.when(couponService.getCouponPolicyById(1L)).thenReturn(Optional.of(couponPolicy));

        mockMvc.perform(get("/admin/coupon-policies/{couponId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.couponId").value(1L))
                .andExpect(jsonPath("$.couponName").value("Found Coupon"));
    }

    @Test
    @DisplayName("관리자: 단일 쿠폰 정책 조회 - 미존재")
    void testGetCouponPolicyById_NotFound() throws Exception {
        Mockito.when(couponService.getCouponPolicyById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/admin/coupon-policies/{couponId}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("관리자: 쿠폰 정책 삭제")
    void testDeleteCouponPolicy() throws Exception {
        Long couponPolicyId = 1L;

        mockMvc.perform(delete("/admin/coupon-policies/{couponId}", couponPolicyId))
                .andExpect(status().isNoContent());

        Mockito.verify(couponService).deleteCouponPolicy(couponPolicyId);
    }
}
