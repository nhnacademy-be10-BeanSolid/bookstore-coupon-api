package com.nhnacademy.dto.request;

import com.nhnacademy.domain.enumtype.CouponDiscountType;
import com.nhnacademy.domain.enumtype.CouponScope;
import com.nhnacademy.domain.enumtype.CouponType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "쿠폰 정책 생성 요청 DTO")
public class CouponPolicyRequestDto {

    @NotBlank
    @Schema(description = "쿠폰 이름", example = "도서 할인 쿠폰")
    private String couponName;

    @NotNull
    @Schema(description = "쿠폰 할인 타입", example = "PERCENT")
    private CouponDiscountType couponDiscountType;

    @Min(0)
    @Schema(description = "쿠폰 할인 금액 또는 비율", example = "10")
    private Integer couponDiscountAmount;

    @Min(0)
    @Schema(description = "쿠폰 적용 최소 주문 금액", example = "20000")
    private Integer couponMinimumOrderAmount;

    @Min(0)
    @Schema(description = "쿠폰 최대 할인 금액", example = "50000")
    private Integer couponMaximumDiscountAmount;

    @NotNull
    @Schema(description = "쿠폰 적용 범위", example = "BOOK")
    private CouponScope couponScope;

    @Schema(description = "쿠폰 만료 일자 (null이면 기한 없음)", example = "2025-12-31T23:59:59", nullable = true)
    private LocalDateTime couponExpiredAt;

    @Schema(description = "쿠폰 발급 기간 (일 단위, null이면 기간 무제한)", example = "30", nullable = true)
    private Integer couponIssuePeriod;

    @NotNull
    @Schema(description = "쿠폰 타입", example = "WELCOME")
    private CouponType couponType;

    @Schema(description = "적용 도서 ID 목록", example = "[101, 102, 103]", nullable = true)
    private List<Long> bookIds;

    @Schema(description = "적용 카테고리 ID 목록", example = "[10, 20]", nullable = true)
    private List<Long> categoryIds;
}
