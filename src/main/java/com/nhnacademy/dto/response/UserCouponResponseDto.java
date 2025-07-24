package com.nhnacademy.dto.response;

import com.nhnacademy.domain.UserCouponList;
import com.nhnacademy.domain.enumtype.UserCouponStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사용자 쿠폰 응답 DTO")
public class UserCouponResponseDto {

    @Schema(description = "사용자 쿠폰 ID", example = "100")
    private Long userCouponId;

    @Schema(description = "사용자 번호", example = "2001")
    private Long userNo;

    @Schema(description = "쿠폰 정책 ID", example = "3001")
    private Long couponPolicyId;

    @Schema(description = "쿠폰 이름", example = "10% 할인 쿠폰")
    private String couponName;

    @Schema(description = "쿠폰 할인 금액", example = "10000")
    private int couponDiscountAmount;

    @Schema(description = "쿠폰 발급 일자", example = "2025-07-24T10:41:00")
    private LocalDateTime issuedAt;

    @Schema(description = "쿠폰 만료 일자", example = "2025-12-31T23:59:59")
    private LocalDateTime expiredAt;

    @Schema(description = "쿠폰 사용 일자", example = "2025-08-01T15:30:00", nullable = true)
    private LocalDateTime usedAt;

    @Schema(description = "쿠폰 상태", example = "ACTIVE")
    private UserCouponStatus status;

    @Schema(description = "주문 ID", example = "101", nullable = true)
    private Long orderId;

    public static UserCouponResponseDto from(UserCouponList userCoupon) {
        Long couponPolicyId = null;
        String couponName = null;
        int couponDiscountAmount = 0;

        if (userCoupon.getCouponPolicy() != null) {
            couponPolicyId = userCoupon.getCouponPolicy().getCouponId();
            couponName = userCoupon.getCouponPolicy().getCouponName();
            couponDiscountAmount = userCoupon.getCouponPolicy().getCouponDiscountAmount();
        }

        return UserCouponResponseDto.builder()
                .userCouponId(userCoupon.getUserCouponId())
                .userNo(userCoupon.getUserNo())
                .couponPolicyId(couponPolicyId)
                .couponName(couponName)
                .couponDiscountAmount(couponDiscountAmount)
                .issuedAt(userCoupon.getIssuedAt())
                .expiredAt(userCoupon.getExpiredAt())
                .usedAt(userCoupon.getUsedAt())
                .status(userCoupon.getStatus())
                .orderId(userCoupon.getOrderId())
                .build();
    }
}
