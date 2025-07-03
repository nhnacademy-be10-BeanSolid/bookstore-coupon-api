package com.nhnacademy.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "coupon_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@IdClass(CouponUser.class)
@Builder
public class CouponUser {
    @Id
    @Column(name = "coupon_id")
    private Long couponId;

    @Id
    @Column(name = "user_id", length = 20)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("couponId")
    @JoinColumn(name = "coupon_id", insertable = false, updatable = false)
    private CouponPolicy couponPolicy;
}