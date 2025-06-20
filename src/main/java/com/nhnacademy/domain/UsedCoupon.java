package com.nhnacademy.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "used_coupon")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UsedCoupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_coupon_id")
    private Long userCouponId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "id", nullable = true)
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private CouponPolicy couponPolicy;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserCouponStatus status;

    public void use() {
        if (this.status == UserCouponStatus.ACTIVE && this.expiredAt.isAfter(LocalDateTime.now())) {
            this.status = UserCouponStatus.USED;
            this.usedAt = LocalDateTime.now();
        } else {
            throw new IllegalStateException("쿠폰을 사용할 수 없는 상태입니다 (이미 사용되었거나 만료).");
        }
    }
}