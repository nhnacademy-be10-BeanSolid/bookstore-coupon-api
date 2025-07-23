package com.nhnacademy.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "coupon_books",
        uniqueConstraints = @UniqueConstraint(columnNames = {"coupon_id", "book_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CouponBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_book_id")
    private Long couponBookId;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private CouponPolicy couponPolicy;

    @Setter
    @Column(name = "book_id")
    private Long bookId;

}