package com.nhnacademy.domain;

import com.nhnacademy.domain.enumtype.CouponDiscountType;
import com.nhnacademy.domain.enumtype.CouponScope;
import com.nhnacademy.domain.enumtype.CouponType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Schema(description = "쿠폰 정책 엔티티")
public class CouponPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    @Schema(description = "쿠폰 정책 식별자", example = "1")
    private Long couponId;    //쿠폰 정책 식별자

    @Column(name = "coupon_name", nullable = false)
    @Schema(description = "쿠폰 정책 이름", example = "생일 할인 쿠폰")
    private String couponName;  // 쿠폰 정책 이름

    @Enumerated(EnumType.STRING)
    @Column(name = "coupon_type", nullable = false)
    @Schema(description = "쿠폰 유형 (WELCOME, BIRTHDAY, GENERAL 등)", example = "WELCOME")
    private CouponType couponType; // 쿠폰 유형

    @Enumerated(EnumType.STRING)
    @Column(name = "coupon_discount_type", nullable = false)
    @Schema(description = "쿠폰 할인 유형", example = "PERCENT")
    private CouponDiscountType couponDiscountType; // 쿠폰 할인 유형

    @Column(name = "coupon_discount_amount", nullable = false)
    @Schema(description = "쿠폰 할인 정도", example = "10")
    private Integer couponDiscountAmount;  // 쿠폰 할인 정도

    @Column(name = "coupon_minimum_order_amount")
    @Schema(description = "쿠폰 최소 구매 조건 금액", example = "20000", nullable = true)
    private Integer couponMinimumOrderAmount; // 쿠폰 최소 구매 조건

    @Column(name = "coupon_maximum_discount_amount")
    @Schema(description = "쿠폰 최대 할인 금액", example = "50000", nullable = true)
    private Integer couponMaximumDiscountAmount; // 쿠폰 최대 할인

    @Enumerated(EnumType.STRING)
    @Column(name = "coupon_scope", nullable = false)
    @Schema(description = "쿠폰 적용 범위", example = "BOOK")
    private CouponScope couponScope;  // 쿠폰 적용 범위

    @Column(name = "coupon_created_at", nullable = false)
    @Schema(description = "쿠폰 정책 생성 일자", example = "2025-07-24T10:48:00")
    private LocalDateTime couponCreatedAt;  // 쿠폰 정책 생성 일자

    @Column(name = "coupon_expired_at")
    @Schema(description = "쿠폰 만료 일자 (null이면 만료 없음)", example = "2025-12-31T23:59:59", nullable = true)
    private LocalDateTime couponExpiredAt;  // 쿠폰 만료 일자

    @Column(name = "coupon_issue_period")
    @Schema(description = "쿠폰 발행 유효 기간 (일 단위, null이면 기간 무제한)", example = "30", nullable = true)
    private Integer couponIssuePeriod;  // 쿠폰 발행 유효 기간

    @PrePersist
    protected void onCreate() {
        this.couponCreatedAt = LocalDateTime.now();
    }
}
