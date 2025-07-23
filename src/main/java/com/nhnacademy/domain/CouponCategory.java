package com.nhnacademy.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "coupon_categories",
        uniqueConstraints = @UniqueConstraint(columnNames = {"coupon_id", "category_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CouponCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_category_id")
    private Long couponCategoryId;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private CouponPolicy couponPolicy;

    @Setter
    @Column(name = "category_id")
    private Long categoryId;
}