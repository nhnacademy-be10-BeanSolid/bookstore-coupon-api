package com.nhnacademy.controller.swagger;

import com.nhnacademy.common.dto.ErrorResponse;
import com.nhnacademy.dto.request.CouponPolicyRequestDto;
import com.nhnacademy.dto.response.CouponPolicyResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Coupon Admin API", description = "관리자 쿠폰 정책/발급 관리 API")
@RequestMapping("/admin")
public interface CouponAdminControllerDoc {

    @Operation(summary = "전체 사용자에게 쿠폰 발급", description = "특정 쿠폰 정책에 따라 모든 활성 사용자에게 쿠폰 발급 프로세스를 시작합니다.")
    @ApiResponse(responseCode = "202", description = "쿠폰 발급 프로세스 시작 성공 (비동기 처리)")
    @ApiResponse(responseCode = "404", description = "쿠폰 정책을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "Not Found",
                            value = "{\"errorCode\": \"NOT_FOUND\", \"errorMessage\": \"쿠폰 정책을 찾을 수 없습니다: couponPolicyId=1\"}"
                    )))
    @ApiResponse(responseCode = "409", description = "쿠폰 정책이 이미 만료되었거나 부적절한 범위",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "Conflict",
                            value = "{\"errorCode\": \"CONFLICT\", \"errorMessage\": \"쿠폰 정책이 만료되었거나 전체 발급 대상이 아닙니다.\"}"
                    )))
    @PostMapping("/issue-all/{couponPolicyId}")
    ResponseEntity<Void> startIssuingCouponsToAllUsers(
            @Parameter(description = "전체 사용자에게 발급할 쿠폰 정책 ID", example = "1") @PathVariable Long couponPolicyId);

    @Operation(summary = "특정 도서에 쿠폰 발급", description = "특정 도서와 관련된 쿠폰을 발급합니다.")
    @ApiResponse(responseCode = "202", description = "도서 쿠폰 발급 성공")
    @ApiResponse(responseCode = "404", description = "쿠폰 정책 또는 도서를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "Policy Not Found",
                                    value = "{\"errorCode\": \"NOT_FOUND\", \"errorMessage\": \"쿠폰 정책을 찾을 수 없습니다: couponPolicyId=2\"}"
                            ),
                            @ExampleObject(name = "Book Not Found",
                                    value = "{\"errorCode\": \"NOT_FOUND\", \"errorMessage\": \"도서를 찾을 수 없습니다: bookId=100\"}"
                            )
                    }))
    @ApiResponse(responseCode = "409", description = "쿠폰 정책이 만료되었거나 도서 범위가 아님",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "Conflict",
                            value = "{\"errorCode\": \"CONFLICT\", \"errorMessage\": \"쿠폰 정책이 만료되었거나 도서 전용 쿠폰이 아닙니다.\"}"
                    )))
    @PostMapping("/issue-book")
    ResponseEntity<Void> issueCouponToBook(
            @Parameter(description = "발급할 쿠폰 정책 ID", example = "2") @RequestParam Long couponPolicyId,
            @Parameter(description = "쿠폰을 발급할 도서 ID", example = "100") @RequestParam Long bookId);

    @Operation(summary = "특정 사용자에게 쿠폰 발급", description = "지정된 사용자에게 특정 쿠폰 정책에 따라 쿠폰을 발급합니다.")
    @ApiResponse(responseCode = "202", description = "사용자에게 쿠폰 발급 성공")
    @ApiResponse(responseCode = "404", description = "쿠폰 정책 또는 사용자를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "Policy Not Found",
                                    value = "{\"errorCode\": \"NOT_FOUND\", \"errorMessage\": \"쿠폰 정책을 찾을 수 없습니다: couponPolicyId=3\"}"
                            ),
                            @ExampleObject(name = "User Not Found",
                                    value = "{\"errorCode\": \"NOT_FOUND\", \"errorMessage\": \"사용자를 찾을 수 없습니다: userNo=1\"}"
                            )
                    }))
    @ApiResponse(responseCode = "409", description = "쿠폰 정책이 만료되었거나 이미 발급된 쿠폰",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "Expired Policy",
                                    value = "{\"errorCode\": \"CONFLICT\", \"errorMessage\": \"만료된 쿠폰 정책입니다.\"}"
                            ),
                            @ExampleObject(name = "Coupon Already Issued",
                                    value = "{\"errorCode\": \"CONFLICT\", \"errorMessage\": \"이미 발급된 쿠폰입니다.\"}"
                            )
                    }))
    @PostMapping("/issue-to-user")
    ResponseEntity<Void> issueCouponToUser(
            @Parameter(description = "쿠폰을 발급받을 사용자 번호", example = "1") @RequestParam Long userNo,
            @Parameter(description = "발급할 쿠폰 정책 ID", example = "3") @RequestParam Long couponPolicyId);

    @Operation(summary = "쿠폰 정책 생성", description = "새로운 쿠폰 정책을 시스템에 생성합니다. (관리자 전용)")
    @ApiResponse(responseCode = "200", description = "쿠폰 정책 생성 성공")
    @ApiResponse(responseCode = "400", description = "유효하지 않은 요청 데이터",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "Invalid Request",
                            value = "{\"errorCode\": \"BAD_REQUEST\", \"errorMessage\": \"필수 값이 누락되었거나 형식이 올바르지 않습니다.\"}"
                    )))
    @PostMapping("/coupon-policies")
    ResponseEntity<Void> createCouponPolicy(
            @RequestBody(description = "생성할 쿠폰 정책의 상세 정보", required = true)
            CouponPolicyRequestDto request, BindingResult bindingResult);

    @Operation(summary = "모든 쿠폰 정책 조회", description = "시스템에 등록된 모든 쿠폰 정책 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "모든 쿠폰 정책 조회 성공",
            content = @Content(schema = @Schema(implementation = CouponPolicyResponseDto.class)))
    @GetMapping("/coupon-policies")
    ResponseEntity<List<CouponPolicyResponseDto>> getAllCouponPolicies();

    @Operation(summary = "특정 쿠폰 정책 상세 조회", description = "특정 쿠폰 ID를 사용하여 쿠폰 정책의 상세 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "쿠폰 정책 상세 조회 성공",
            content = @Content(schema = @Schema(implementation = CouponPolicyResponseDto.class)))
    @ApiResponse(responseCode = "404", description = "쿠폰 정책을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "Not Found",
                            value = "{\"errorCode\": \"NOT_FOUND\", \"errorMessage\": \"쿠폰 정책을 찾을 수 없습니다: couponId=1\"}"
                    )))
    @GetMapping("/coupon-policies/{couponId}")
    ResponseEntity<CouponPolicyResponseDto> getCouponPolicyById(
            @Parameter(description = "조회할 쿠폰 정책 ID", example = "1") @PathVariable Long couponId);

    @Operation(summary = "쿠폰 정책 삭제", description = "특정 쿠폰 정책을 시스템에서 삭제합니다. (관리자 전용)")
    @ApiResponse(responseCode = "204", description = "쿠폰 정책 삭제 성공 (응답 본문 없음)")
    @ApiResponse(responseCode = "404", description = "삭제할 쿠폰 정책을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "Not Found",
                            value = "{\"errorCode\": \"NOT_FOUND\", \"errorMessage\": \"삭제할 쿠폰 정책을 찾을 수 없습니다: couponId=1\"}"
                    )))
    @DeleteMapping("/coupon-policies/{couponId}")
    ResponseEntity<Void> deleteCouponPolicy(
            @Parameter(description = "삭제할 쿠폰 정책 ID", example = "1") @PathVariable Long couponId);
}