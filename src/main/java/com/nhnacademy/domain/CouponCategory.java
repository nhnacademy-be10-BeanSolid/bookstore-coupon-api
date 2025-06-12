package com.nhnacademy.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "coupon_category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@IdClass(CouponCategoryId.class)
@Builder
public class CouponCategory {
    @Id
    @Column(name = "coupon_id")
    private Long couponId;

    @Id
    @Column(name = "category_id")
    private Long categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("couponId")
    @JoinColumn(name = "coupon_id")
    private CouponPolicy couponPolicy;
}