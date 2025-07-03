package com.nhnacademy.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "coupon_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@IdClass(CouponUser.class)
@Builder
@EqualsAndHashCode
public class CouponUser implements Serializable {
    private static final long serialVersionUID = 1L;

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