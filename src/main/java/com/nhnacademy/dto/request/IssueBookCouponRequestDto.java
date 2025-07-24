package com.nhnacademy.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "특정 도서 쿠폰 발급 요청 DTO")
public class IssueBookCouponRequestDto {

    @Schema(description = "사용자 ID", example = "1001")
    private Long userId;

    @Schema(description = "도서 ID", example = "2001")
    private Long bookId;

    @Schema(description = "쿠폰 정책 ID", example = "3001")
    private Long couponPolicyId;
}
