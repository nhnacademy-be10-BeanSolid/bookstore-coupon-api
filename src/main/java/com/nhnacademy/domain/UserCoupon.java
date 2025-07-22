package com.nhnacademy.domain;

import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_coupons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCoupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_coupon_id")
    private Long userCouponId;

    @Column(name = "user_no")
    private Long userNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private CouponPolicy couponPolicy;

    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserCouponStatus status;

    @Column(name = "order_id")
    private Long orderId;

    public void use() {
        if (this.status == UserCouponStatus.ACTIVE && this.expiredAt.isAfter(LocalDateTime.now())) {
            this.status = UserCouponStatus.USED;
            this.usedAt = LocalDateTime.now();
        }
    }
}
