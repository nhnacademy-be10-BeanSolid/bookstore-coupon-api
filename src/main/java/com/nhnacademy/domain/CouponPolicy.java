package com.nhnacademy.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupon")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CouponPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private Long couponId;    //쿠폰 정책 식별자

    @Column(name = "coupon_name", nullable = false, length = 255)
    private String couponName;  // 쿠폰 정책 이름

    @Enumerated(EnumType.STRING)
    @Column(name = "coupon_type", nullable = false, length = 20)
    private CouponType couponType; // 쿠폰 유형 (WELCOME, BIRTHDAY, GENERAL 등)

    @Enumerated(EnumType.STRING)
    @Column(name = "coupon_discount_type", nullable = false, length = 20)
    private CouponDiscountType couponDiscountType; // 쿠폰 할인 유형

    @Column(name = "coupon_discount_amount", nullable = false)
    private Integer couponDiscountAmount;  // 쿠폰 할인 정도

    @Column(name = "coupon_minimum_order_amount")
    private Integer couponMinimumOrderAmount; // 쿠폰 최소구매조권

    @Column(name = "coupon_maximum_discount_amount")
    private Integer couponMaximumDiscountAmount; // 쿠폰 최대 할인

    @Enumerated(EnumType.STRING)
    @Column(name = "coupon_scope", nullable = false, length = 30)
    private CouponScope couponScope;  // 쿠폰 적용 범위

    @Column(name = "coupon_created_at", nullable = false)
    private LocalDateTime couponCreatedAt;  // 쿠폰 발급일

    @Column(name = "coupon_expired_at")
    private LocalDateTime couponExpiredAt;  // 쿠폰 만료일

    @Column(name = "coupon_issue_period")
    private Integer couponIssuePeriod;  // 쿠폰 발행 유효 기간

    @PrePersist
    protected void onCreate() {
        this.couponCreatedAt = LocalDateTime.now();
    }
}