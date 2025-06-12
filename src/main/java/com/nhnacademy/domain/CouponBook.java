package com.nhnacademy.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "coupon_book")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@IdClass(CouponBookId.class)
@Builder
public class CouponBook {
    @Id
    @Column(name = "coupon_id")
    private Long couponId;

    @Id
    @Column(name = "book_id")
    private Long bookId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("couponId")
    @JoinColumn(name = "coupon_id")
    private CouponPolicy couponPolicy;
}
