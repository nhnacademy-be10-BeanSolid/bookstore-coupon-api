package com.nhnacademy.domain;

import com.nhnacademy.domain.enumtype.UserCouponStatus;
import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_coupon_list")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCouponList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_coupon_list_id")
    private Long userCouponId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private CouponPolicy couponPolicy;

    @Column(name = "user_no", nullable = false)
    private Long userNo;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserCouponStatus status;

    @Column(name = "expired_at", updatable = false)
    private LocalDateTime expiredAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    public void use() {
        if (this.status == UserCouponStatus.ACTIVE && this.expiredAt.isAfter(LocalDateTime.now())) {
            this.status = UserCouponStatus.USED;
            this.usedAt = LocalDateTime.now();
        }
    }
}
