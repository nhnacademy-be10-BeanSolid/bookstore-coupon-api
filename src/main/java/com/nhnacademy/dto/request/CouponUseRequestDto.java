package com.nhnacademy.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponUseRequestDto {
    private Long orderId;
}
