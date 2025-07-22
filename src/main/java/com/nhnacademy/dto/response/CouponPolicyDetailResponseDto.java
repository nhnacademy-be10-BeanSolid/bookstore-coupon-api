package com.nhnacademy.dto.response;

import com.nhnacademy.domain.enumtype.CouponDiscountType;
import com.nhnacademy.domain.enumtype.CouponScope;
import com.nhnacademy.domain.CouponPolicy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class CouponPolicyDetailResponseDto {
    private Long couponId;
    private String couponName;
    private CouponDiscountType couponDiscountType;
    private int couponDiscountAmount;
    private Integer couponMinimumOrderAmount;
    private Integer couponMaximumDiscountAmount;
    private CouponScope couponScope;
    private LocalDateTime couponExpiredAt;
    private Integer couponIssuePeriod;
    private List<Long> bookIds;
    private List<Long> categoryIds;

    public static CouponPolicyDetailResponseDto from(CouponPolicy policy, List<Long> bookIds, List<Long> categoryIds) {
        return CouponPolicyDetailResponseDto.builder()
                .couponId(policy.getCouponId())   // 쿠폰 식별자
                .couponName(policy.getCouponName())  // 쿠폰 이름
                .couponDiscountType(policy.getCouponDiscountType()) // 쿠폰 할인 유형
                .couponDiscountAmount(policy.getCouponDiscountAmount()) // 쿠폰할인 금액/비율
                .couponMinimumOrderAmount(policy.getCouponMinimumOrderAmount())  //쿠폰 최소주문금액
                .couponMaximumDiscountAmount(policy.getCouponMaximumDiscountAmount()) // 쿠폰 최대 할인금액
                .couponScope(policy.getCouponScope()) // 쿠폰 적용 범위
                .couponExpiredAt(policy.getCouponExpiredAt()) // 쿠폰 만료일시
                .couponIssuePeriod(policy.getCouponIssuePeriod()) //쿠폰 발행 기간
                .bookIds(bookIds) // 도서 ID 목록
                .categoryIds(categoryIds)  // 카테고리 ID 목록
                .build();
    }
}
