package com.nhnacademy.domain;

import lombok.*;
import java.io.Serializable;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CouponBookId implements Serializable {
    private Long couponId;
    private Long bookId;
}
