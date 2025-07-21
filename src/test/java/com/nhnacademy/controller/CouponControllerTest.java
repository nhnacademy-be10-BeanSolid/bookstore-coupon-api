package com.nhnacademy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.common.exception.CouponAlreadyExistException;
import com.nhnacademy.dto.request.CouponUseRequest;
import com.nhnacademy.dto.response.UserCouponResponse;
import com.nhnacademy.domain.*;
import com.nhnacademy.exception.*;
import com.nhnacademy.service.CouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull; // isNull 임포트 확인
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CouponController.class)
@DisplayName("CouponController 테스트")
public class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CouponService couponService;

    private CouponPolicy testCouponPolicy;
    private UsedCoupon testUsedCoupon;
    private UserCouponResponse testUserCouponResponse;
    private final String testUserNo = "testUser1";
    private final Long testCouponPolicyId = 1L;
    private final Long testUserCouponId = 100L;

    @BeforeEach
    void setUp() {
        testCouponPolicy = CouponPolicy.builder()
                .couponId(testCouponPolicyId)
                .couponName("Test Policy")
                .couponDiscountType(CouponDiscountType.PERCENT)
                .couponDiscountAmount(10)
                .couponMinimumOrderAmount(10000)
                .couponMaximumDiscountAmount(5000)
                .couponScope(CouponScope.ALL)
                .couponCreatedAt(LocalDateTime.now())
                .couponExpiredAt(LocalDateTime.now().plusDays(30))
                .couponIssuePeriod(null)
                .couponType(CouponType.GENERAL)
                .build();

        testUsedCoupon = UsedCoupon.builder()
                .userCouponId(testUserCouponId)
                .userNo(testUserNo)
                .couponPolicy(testCouponPolicy)
                .issuedAt(LocalDateTime.now().minusDays(5))
                .expiredAt(LocalDateTime.now().plusDays(25))
                .status(UserCouponStatus.ACTIVE)
                .orderId(null)
                .usedAt(null)
                .build();

        testUserCouponResponse = UserCouponResponse.from(testUsedCoupon);
    }

    // --- 헬퍼 메소드 (이전과 동일) ---
    private ResultActions performPostRequest(String url, Object requestBody, Object... pathVariables) throws Exception {
        return mockMvc.perform(post(url, pathVariables)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)));
    }

    private ResultActions performGetRequest(String url, Object... pathVariables) throws Exception {
        return mockMvc.perform(get(url, pathVariables));
    }

    @Nested
    @DisplayName("쿠폰 정책 관련 API")
    class CouponPolicyApiTests {

//        @Test
//        @DisplayName("POST /policy - 쿠폰 정책 생성 성공")
//        void createCouponPolicy_success() throws Exception {
//            CouponPolicyRequest request = new CouponPolicyRequest(
//                    "새 쿠폰", CouponDiscountType.AMOUNT, 1000, 5000, null,
//                    CouponScope.ALL, LocalDateTime.now().plusMonths(1), 30,
//                    Collections.emptyList(), Collections.emptyList(), CouponType.GENERAL);
//
//            when(couponService.createCouponPolicy(
//                    anyString(), any(CouponDiscountType.class), anyInt(),
//                    any(), any(), any(CouponScope.class), any(LocalDateTime.class), any(),
//                    anyList(), anyList(), any(CouponType.class)))
//                    .thenReturn(testCouponPolicy);
//
//            performPostRequest("/coupons/policy", request)
//                    .andExpect(status().isCreated())
//                    .andExpect(jsonPath("$.couponName").value(testCouponPolicy.getCouponName()));
//
//            verify(couponService, times(1)).createCouponPolicy(
//                    anyString(), any(CouponDiscountType.class), anyInt(),
//                    any(), any(), any(CouponScope.class), any(LocalDateTime.class), any(),
//                    anyList(), anyList(), any(CouponType.class));
//        }


        @Test
        @DisplayName("GET /policy - 모든 쿠폰 정책 조회 성공")
        void getAllCouponPolicies_success() throws Exception {
            List<CouponPolicy> policies = Arrays.asList(testCouponPolicy, testCouponPolicy);
            when(couponService.getAllCouponPolicies()).thenReturn(policies);

            performGetRequest("/coupons/policy")
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].couponName").value(testCouponPolicy.getCouponName()));

            verify(couponService, times(1)).getAllCouponPolicies();
        }

        @Test
        @DisplayName("GET /policy/{policyId} - 특정 쿠폰 정책 조회 성공")
        void getCouponPolicy_success() throws Exception {
            when(couponService.getCouponPolicy(testCouponPolicyId)).thenReturn(testCouponPolicy);

            performGetRequest("/coupons/policy/{policyId}", testCouponPolicyId)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.couponId").value(testCouponPolicy.getCouponId()))
                    .andExpect(jsonPath("$.couponName").value(testCouponPolicy.getCouponName()));

            verify(couponService, times(1)).getCouponPolicy(testCouponPolicyId);
        }

        @Test
        @DisplayName("GET /policy/{policyId} - 특정 쿠폰 정책 조회 실패 (찾을 수 없음)")
        void getCouponPolicy_notFound() throws Exception {
            doThrow(new CouponNotFoundException("쿠폰 정책을 찾을 수 없습니다.")).when(couponService).getCouponPolicy(anyLong());

            performGetRequest("/coupons/policy/{policyId}", 999L)
                    .andExpect(status().isNotFound());

            verify(couponService, times(1)).getCouponPolicy(999L);
        }
    }

    @Nested
    @DisplayName("사용자 쿠폰 발급 관련 API")
    class UserCouponIssueApiTests {

        @Test
        @DisplayName("POST /users/{userNo}/issue/{couponPolicyId} - 사용자에게 쿠폰 발급 성공")
        void issueCouponToUser_success() throws Exception {
            when(couponService.issueCouponToUser(testUserNo, testCouponPolicyId)).thenReturn(testUsedCoupon);

            mockMvc.perform(post("/coupons/users/{userNo}/issue/{couponPolicyId}", testUserNo, testCouponPolicyId))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.userNo").value(testUserNo))
                    .andExpect(jsonPath("$.couponPolicyId").value(testCouponPolicyId));

            verify(couponService, times(1)).issueCouponToUser(testUserNo, testCouponPolicyId);
        }

        @Test
        @DisplayName("POST /users/{userNo}/issue/{couponPolicyId} - 쿠폰 발급 실패 (정책 찾을 수 없음)")
        void issueCouponToUser_policyNotFound() throws Exception {
            doThrow(new CouponNotFoundException("정책 없음")).when(couponService).issueCouponToUser(anyString(), anyLong());

            mockMvc.perform(post("/coupons/users/{userNo}/issue/{couponPolicyId}", testUserNo, 999L))
                    .andExpect(status().isNotFound());

            verify(couponService, times(1)).issueCouponToUser(testUserNo, 999L);
        }

        @Test
        @DisplayName("POST /users/{userNo}/issue/{couponPolicyId} - 쿠폰 발급 실패 (정책 만료)")
        void issueCouponToUser_policyExpired() throws Exception {
            doThrow(new CouponExpiredException("정책 만료")).when(couponService).issueCouponToUser(anyString(), anyLong());

            mockMvc.perform(post("/coupons/users/{userNo}/issue/{couponPolicyId}", testUserNo, testCouponPolicyId))
                    .andExpect(status().isBadRequest());

            verify(couponService, times(1)).issueCouponToUser(testUserNo, testCouponPolicyId);
        }

        @Test
        @DisplayName("POST /users/{userNo}/issue-welcome - 웰컴 쿠폰 발급 성공")
        void issueWelcomeCoupon_success() throws Exception {
            when(couponService.issueWelcomeCoupon(testUserNo)).thenReturn(testUsedCoupon);

            mockMvc.perform(post("/coupons/users/{userNo}/issue-welcome", testUserNo))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.userNo").value(testUserNo))
                    .andExpect(jsonPath("$.couponName").value(testCouponPolicy.getCouponName()));

            verify(couponService, times(1)).issueWelcomeCoupon(testUserNo);
        }

        @Test
        @DisplayName("POST /users/{userNo}/issue-welcome - 웰컴 쿠폰 발급 실패 (이미 발급됨)")
        void issueWelcomeCoupon_alreadyIssued() throws Exception {
            doThrow(new CouponAlreadyExistException("이미 발급됨")).when(couponService).issueWelcomeCoupon(anyString());

            mockMvc.perform(post("/coupons/users/{userNo}/issue-welcome", testUserNo))
                    .andExpect(status().isConflict());

            verify(couponService, times(1)).issueWelcomeCoupon(testUserNo);
        }

        @Test
        @DisplayName("POST /users/{userNo}/issue-birthday - 생일 쿠폰 발급 성공")
        void issueBirthdayCoupon_success() throws Exception {
            LocalDate userBirthDate = LocalDate.now().withMonth(7).withDayOfMonth(1);
            when(couponService.issueBirthdayCoupon(eq(testUserNo), any(LocalDate.class))).thenReturn(testUsedCoupon);

            mockMvc.perform(post("/coupons/users/{userNo}/issue-birthday", testUserNo)
                            .param("birthMonth", String.valueOf(userBirthDate.getMonthValue())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.userNo").value(testUserNo));

            verify(couponService, times(1)).issueBirthdayCoupon(eq(testUserNo), any(LocalDate.class));
        }

        @Test
        @DisplayName("POST /users/{userNo}/issue-birthday - 생일 쿠폰 발급 실패 (이미 발급됨)")
        void issueBirthdayCoupon_alreadyIssued() throws Exception {
            doThrow(new CouponAlreadyExistException("이미 발급됨")).when(couponService).issueBirthdayCoupon(anyString(), any(LocalDate.class));

            mockMvc.perform(post("/coupons/users/{userNo}/issue-birthday", testUserNo)
                            .param("birthMonth", "7"))
                    .andExpect(status().isConflict());

            verify(couponService, times(1)).issueBirthdayCoupon(eq(testUserNo), any(LocalDate.class));
        }
    }

    @Nested
    @DisplayName("사용자 쿠폰 조회 API")
    class UserCouponQueryApiTests {

        @Test
        @DisplayName("GET /users/{userNo}/active - 사용자 활성 쿠폰 조회 성공")
        void getActiveUserCoupons_success() throws Exception {
            List<UsedCoupon> activeCoupons = Arrays.asList(testUsedCoupon, testUsedCoupon);
            when(couponService.getActiveUserCoupons(testUserNo)).thenReturn(activeCoupons);

            performGetRequest("/coupons/users/{userNo}/active", testUserNo)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].userNo").value(testUserNo));

            verify(couponService, times(1)).getActiveUserCoupons(testUserNo);
        }

        @Test
        @DisplayName("GET /users/{userNo}/used - 사용자 사용된 쿠폰 조회 성공")
        void getUsedUserCoupons_success() throws Exception {
            testUsedCoupon.setStatus(UserCouponStatus.USED);
            List<UsedCoupon> usedCoupons = Arrays.asList(testUsedCoupon);
            when(couponService.getUsedUserCoupons(testUserNo)).thenReturn(usedCoupons);

            performGetRequest("/coupons/users/{userNo}/used", testUserNo)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].userNo").value(testUserNo))
                    .andExpect(jsonPath("$[0].status").value("USED"));

            verify(couponService, times(1)).getUsedUserCoupons(testUserNo);
        }
    }

    @Nested
    @DisplayName("쿠폰 사용 및 할인 계산 API")
    class CouponUsageAndCalculationApiTests {

        @Test
        @DisplayName("POST /users/{userNo}/use/{userCouponId} - 쿠폰 사용 성공")
        void useCoupon_success() throws Exception {
            CouponUseRequest request = new CouponUseRequest(12345L);
            doNothing().when(couponService).useCoupon(testUserNo, testUserCouponId, request.getOrderId());

            performPostRequest("/coupons/users/{userNo}/use/{userCouponId}", request, testUserNo, testUserCouponId)
                    .andExpect(status().isOk())
                    .andExpect(content().string("쿠폰이 성공적으로 사용되었습니다."));

            verify(couponService, times(1)).useCoupon(testUserNo, testUserCouponId, request.getOrderId());
        }

        @Test
        @DisplayName("POST /users/{userNo}/use/{userCouponId} - 쿠폰 사용 실패 (쿠폰 찾을 수 없음)")
        void useCoupon_notFound() throws Exception {
            CouponUseRequest request = new CouponUseRequest(12345L);
            doThrow(new UserCouponNotFoundException("쿠폰 없음")).when(couponService).useCoupon(anyString(), anyLong(), anyLong());

            performPostRequest("/coupons/users/{userNo}/use/{userCouponId}", request, testUserNo, 999L)
                    .andExpect(status().isNotFound());

            verify(couponService, times(1)).useCoupon(testUserNo, 999L, request.getOrderId());
        }

        @Test
        @DisplayName("POST /users/{userNo}/use/{userCouponId} - 쿠폰 사용 실패 (이미 사용됨)")
        void useCoupon_alreadyUsed() throws Exception {
            CouponUseRequest request = new CouponUseRequest(12345L);
            doThrow(new CouponAlreadyUsedException("이미 사용됨")).when(couponService).useCoupon(anyString(), anyLong(), anyLong());

            performPostRequest("/coupons/users/{userNo}/use/{userCouponId}", request, testUserNo, testUserCouponId)
                    .andExpect(status().isBadRequest());

            verify(couponService, times(1)).useCoupon(testUserNo, testUserCouponId, request.getOrderId());
        }

        @Test
        @DisplayName("POST /coupons/users/{userNo}/use/{userCouponId} - 쿠폰 사용 실패 (만료됨)")
        void useCoupon_expired() throws Exception {
            CouponUseRequest request = new CouponUseRequest(12345L);
            doThrow(new CouponExpiredException("만료됨")).when(couponService).useCoupon(anyString(), anyLong(), anyLong());

            performPostRequest("/coupons/users/{userNo}/use/{userCouponId}", request, testUserNo, testUserCouponId)
                    .andExpect(status().isBadRequest());

            verify(couponService, times(1)).useCoupon(testUserNo, testUserCouponId, request.getOrderId());
        }

        @Test
        @DisplayName("GET /coupons/users/{userNo}/calculate-discount/{userCouponId} - 할인 금액 계산 성공 (도서/카테고리 ID 없음)")
        void calculateDiscount_success_noBookOrCategoryIds() throws Exception {
            int orderAmount = 10000;
            int expectedDiscount = 1000;
            // null로 전달될 것을 예상
            when(couponService.calculateDiscountAmount(eq(testUserNo), eq(testUserCouponId), eq(orderAmount), isNull(), isNull()))
                    .thenReturn(expectedDiscount);

            mockMvc.perform(get("/coupons/users/{userNo}/calculate-discount/{userCouponId}", testUserNo, testUserCouponId)
                            .param("orderAmount", String.valueOf(orderAmount)))
                    .andExpect(status().isOk())
                    .andExpect(content().string(String.valueOf(expectedDiscount)));

            verify(couponService, times(1)).calculateDiscountAmount(eq(testUserNo), eq(testUserCouponId), eq(orderAmount), isNull(), isNull());
        }

        @Test
        @DisplayName("GET /coupons/users/{userNo}/calculate-discount/{userCouponId} - 할인 금액 계산 성공 (도서 ID 포함)")
        void calculateDiscount_success_withBookIds() throws Exception {
            int orderAmount = 20000;
            int expectedDiscount = 2000;
            List<Long> bookIds = Arrays.asList(1L, 2L);
            when(couponService.calculateDiscountAmount(eq(testUserNo), eq(testUserCouponId), eq(orderAmount), eq(bookIds), isNull()))
                    .thenReturn(expectedDiscount);

            mockMvc.perform(get("/coupons/users/{userNo}/calculate-discount/{userCouponId}", testUserNo, testUserCouponId)
                            .param("orderAmount", String.valueOf(orderAmount))
                            .param("bookIds", "1", "2"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(String.valueOf(expectedDiscount)));

            verify(couponService, times(1)).calculateDiscountAmount(eq(testUserNo), eq(testUserCouponId), eq(orderAmount), eq(bookIds), isNull());
        }

        @Test
        @DisplayName("GET /coupons/users/{userNo}/calculate-discount/{userCouponId} - 할인 금액 계산 성공 (카테고리 ID 포함)")
        void calculateDiscount_success_withCategoryIds() throws Exception {
            int orderAmount = 15000;
            int expectedDiscount = 1500;
            List<Long> categoryIds = Arrays.asList(10L, 11L);
            when(couponService.calculateDiscountAmount(eq(testUserNo), eq(testUserCouponId), eq(orderAmount), isNull(), eq(categoryIds)))
                    .thenReturn(expectedDiscount);

            mockMvc.perform(get("/coupons/users/{userNo}/calculate-discount/{userCouponId}", testUserNo, testUserCouponId)
                            .param("orderAmount", String.valueOf(orderAmount))
                            .param("categoryIds", "10", "11"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(String.valueOf(expectedDiscount)));

            verify(couponService, times(1)).calculateDiscountAmount(eq(testUserNo), eq(testUserCouponId), eq(orderAmount), isNull(), eq(categoryIds));
        }

        @Test
        @DisplayName("GET /coupons/users/{userNo}/calculate-discount/{userCouponId} - 할인 계산 실패 (쿠폰 찾을 수 없음)")
        void calculateDiscount_couponNotFound() throws Exception {
            doThrow(new UserCouponNotFoundException("쿠폰 없음")).when(couponService).calculateDiscountAmount(anyString(), anyLong(), anyInt(), any(), any());

            mockMvc.perform(get("/coupons/users/{userNo}/calculate-discount/{userCouponId}", testUserNo, 999L)
                            .param("orderAmount", "10000"))
                    .andExpect(status().isNotFound());

            verify(couponService, times(1)).calculateDiscountAmount(eq(testUserNo), eq(999L), anyInt(), any(), any());
        }

        @Test
        @DisplayName("GET /coupons/users/{userNo}/calculate-discount/{userCouponId} - 할인 계산 실패 (쿠폰 적용 불가)")
        void calculateDiscount_notApplicable() throws Exception {
            doThrow(new CouponNotApplicableException("적용 불가")).when(couponService).calculateDiscountAmount(anyString(), anyLong(), anyInt(), any(), any());

            mockMvc.perform(get("/coupons/users/{userNo}/calculate-discount/{userCouponId}", testUserNo, testUserCouponId)
                            .param("orderAmount", "1000"))
                    .andExpect(status().isBadRequest());

            verify(couponService, times(1)).calculateDiscountAmount(eq(testUserNo), eq(testUserCouponId), anyInt(), any(), any());
        }

        @Test
        @DisplayName("GET /coupons/users/{userNo}/calculate-discount/{userCouponId} - 할인 계산 실패 (쿠폰 만료)")
        void calculateDiscount_expired() throws Exception {
            doThrow(new CouponExpiredException("만료됨")).when(couponService).calculateDiscountAmount(anyString(), anyLong(), anyInt(), any(), any());

            mockMvc.perform(get("/coupons/users/{userNo}/calculate-discount/{userCouponId}", testUserNo, testUserCouponId)
                            .param("orderAmount", "10000"))
                    .andExpect(status().isBadRequest());

            verify(couponService, times(1)).calculateDiscountAmount(eq(testUserNo), eq(testUserCouponId), anyInt(), any(), any());
        }
    }
}
