package com.nhnacademy.domain;

import lombok.*;
import java.io.Serializable;

@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CouponUserId implements Serializable {
    private Long couponId;
    private String userNo;
}