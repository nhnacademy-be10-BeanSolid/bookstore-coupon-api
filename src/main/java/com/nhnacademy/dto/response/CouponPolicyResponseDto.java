package com.nhnacademy.dto.response;

import com.nhnacademy.domain.enumtype.CouponDiscountType;
import com.nhnacademy.domain.enumtype.CouponScope;
import com.nhnacademy.domain.enumtype.CouponType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "쿠폰 정책 응답 DTO")
public class CouponPolicyResponseDto {

    @Schema(description = "쿠폰 정책 ID", example = "1")
    private Long couponId;

    @Schema(description = "쿠폰 이름", example = "카테고리 할인 쿠폰")
    private String couponName;

    @Schema(description = "쿠폰 할인 유형", example = "PERCENT")
    private CouponDiscountType couponDiscountType;

    @Schema(description = "쿠폰 할인 금액 또는 비율", example = "10")
    private int couponDiscountAmount;

    @Schema(description = "쿠폰 최소 주문 금액", example = "20000", nullable = true)
    private Integer couponMinimumOrderAmount;

    @Schema(description = "쿠폰 최대 할인 금액", example = "50000", nullable = true)
    private Integer couponMaximumDiscountAmount;

    @Schema(description = "쿠폰 적용 범위", example = "BOOK")
    private CouponScope couponScope;

    @Schema(description = "쿠폰 만료 일자", example = "2025-12-31T23:59:59", nullable = true)
    private LocalDateTime couponExpiredAt;

    @Schema(description = "쿠폰 발행 유효 기간 (일 단위)", example = "30", nullable = true)
    private Integer couponIssuePeriod;

    @Schema(description = "쿠폰 유형", example = "WELCOME")
    private CouponType couponType;

    @Schema(description = "쿠폰 정책 생성 일자", example = "2025-07-24T10:50:00")
    private LocalDateTime couponCreatedAt;

    @Schema(description = "적용 도서 ID 목록", example = "[101, 102, 103]", nullable = true)
    private List<Long> bookIds;

    @Schema(description = "적용 카테고리 ID 목록", example = "[10, 20]", nullable = true)
    private List<Long> categoryIds;
}
