package com.nhnacademy.domain;

import lombok.*;
import java.io.Serializable;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CouponCategoryId implements Serializable {
    private Long couponId;
    private Long categoryId;
}