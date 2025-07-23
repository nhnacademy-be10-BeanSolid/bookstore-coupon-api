package com.nhnacademy.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "coupon_users",
        uniqueConstraints = @UniqueConstraint(columnNames = {"coupon_id", "user_no"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class CouponUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_user_id")
    private Long couponUserId;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private CouponPolicy couponPolicy;

    @Setter
    @Column(name = "user_no")
    private Long userNo;
}