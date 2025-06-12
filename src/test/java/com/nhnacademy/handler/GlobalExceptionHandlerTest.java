package com.nhnacademy.handler;

import com.fasterxml.jackson.databind.ObjectMapper; // JSON 처리용
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExceptionThrowingController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("GlobalExceptionHandler 테스트")
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("CouponNotFoundException 처리 테스트 - 404 NOT_FOUND")
    public void handleCouponNotFoundExceptionTest() throws Exception {
        mockMvc.perform(get("/test-exception/coupon-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("테스트: 쿠폰을 찾을 수 없음."));
    }

    @Test
    @DisplayName("UserCouponNotFoundException 처리 테스트 - 404 NOT_FOUND")
    public void handleUserCouponNotFoundExceptionTest() throws Exception {
        mockMvc.perform(get("/test-exception/user-coupon-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("테스트: 사용자 쿠폰을 찾을 수 없음."));
    }

    @Test
    @DisplayName("CouponAlreadyUsedException 처리 테스트 - 400 BAD_REQUEST")
    public void handleCouponAlreadyUsedExceptionTest() throws Exception {
        mockMvc.perform(get("/test-exception/coupon-already-used"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("테스트: 이미 사용된 쿠폰."));
    }

    @Test
    @DisplayName("CouponExpiredException 처리 테스트 - 400 BAD_REQUEST")
    public void handleCouponExpiredExceptionTest() throws Exception {
        mockMvc.perform(get("/test-exception/coupon-expired"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("테스트: 만료된 쿠폰."));
    }

    @Test
    @DisplayName("CouponNotApplicableException 처리 테스트 - 400 BAD_REQUEST")
    public void handleCouponNotApplicableExceptionTest() throws Exception {
        mockMvc.perform(get("/test-exception/coupon-not-applicable"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("테스트: 쿠폰 적용 불가."));
    }

    @Test
    @DisplayName("WelcomeCouponPolicyNotFoundException 처리 테스트 - 404 NOT_FOUND")
    public void handleWelcomeCouponPolicyNotFoundExceptionTest() throws Exception {
        mockMvc.perform(get("/test-exception/welcome-coupon-policy-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("테스트: 웰컴 쿠폰 정책을 찾을 수 없음."));
    }

    @Test
    @DisplayName("MethodArgumentNotValidException 처리 테스트 - 400 BAD_REQUEST (유효성 검사 실패)")
    public void handleValidationExceptionsTest() throws Exception {
        TestValidationDto invalidDto = new TestValidationDto();
        String requestBody = objectMapper.writeValueAsString(invalidDto);

        mockMvc.perform(post("/test-exception/validation-error")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("필수 값입니다."));
    }

    @Test
    @DisplayName("모든 RuntimeException 처리 테스트 - 500 INTERNAL_SERVER_ERROR")
    public void handleAllRuntimeExceptionsTest() throws Exception {
        mockMvc.perform(get("/test-exception/runtime-exception"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("알 수 없는 서버 오류가 발생했습니다."));
    }
}