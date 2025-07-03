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
    @Column(name = "user_coupon_id")   // 사용 된 쿠폰
    private Long userCouponId;

    @Column(name = "user_id", nullable = false) // 회원 식별
    private String userId;

    @Column(name = "order_id", nullable = true) // 주문 ID
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)   // 쿠폰 식별
    private CouponPolicy couponPolicy;

    @Column(name = "issued_at", nullable = false)    // 쿠폰 발급
    private LocalDateTime issuedAt;

    @Column(name = "expired_at", nullable = false)    // 쿠폰 만료
    private LocalDateTime expiredAt;

    @Column(name = "used_at")       // 사용 시간
    private LocalDateTime usedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)   // 상태
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