package com.nhnacademy.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupon")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CouponPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private Long couponId;

    @Column(name = "coupon_name", nullable = false, length = 255)
    private String couponName;

    @Enumerated(EnumType.STRING)
    @Column(name = "coupon_discount_type", nullable = false, length = 20)
    private CouponDiscountType couponDiscountType;

    @Column(name = "coupon_discount_amount", nullable = false)
    private Integer couponDiscountAmount;

    @Column(name = "coupon_minimum_order_amount")
    private Integer couponMinimumOrderAmount;

    @Column(name = "coupon_maximum_discount_amount")
    private Integer couponMaximumDiscountAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "coupon_scope", nullable = false, length = 30)
    private CouponScope couponScope;

    @Column(name = "coupon_created_at", nullable = false)
    private LocalDateTime couponCreatedAt;

    @Column(name = "coupon_expired_at")
    private LocalDateTime couponExpiredAt;

    @Column(name = "coupon_issue_period")
    private Integer couponIssuePeriod;

    @PrePersist
    protected void onCreate() {
        this.couponCreatedAt = LocalDateTime.now();
    }
}