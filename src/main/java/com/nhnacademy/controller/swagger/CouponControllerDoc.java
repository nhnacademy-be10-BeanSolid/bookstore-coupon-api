package com.nhnacademy.controller.swagger;

import com.nhnacademy.common.dto.ErrorResponse;
import com.nhnacademy.domain.CouponPolicy;
import com.nhnacademy.dto.request.CouponPolicyRequestDto;
import com.nhnacademy.dto.request.CouponUseRequestDto;
import com.nhnacademy.dto.request.IssueBookCouponRequestDto;
import com.nhnacademy.dto.response.CouponPolicyResponseDto;
import com.nhnacademy.dto.response.UserCouponResponseDto;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Coupon API", description = "사용자 쿠폰 발급/사용/조회 API")
public interface CouponControllerDoc {

    @Operation(summary = "쿠폰 정책 생성", description = "관리자가 새로운 쿠폰 정책을 생성합니다.")
    @ApiResponse(responseCode = "201", description = "쿠폰 정책 생성 성공",
            content = @Content(schema = @Schema(implementation = CouponPolicy.class)))
    @ApiResponse(responseCode = "400", description = "유효하지 않은 요청 데이터",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "Invalid Request",
                            value = "{\"errorCode\": \"BAD_REQUEST\", \"errorMessage\": \"필수 값이 누락되었거나 형식이 올바르지 않습니다.\"}"
                    )))
    @PostMapping("/policy")
    ResponseEntity<CouponPolicy> createCouponPolicy(
            @RequestBody(description = "생성할 쿠폰 정책 상세 정보", required = true)
            CouponPolicyRequestDto request, BindingResult bindingResult);


    @Operation(summary = "전체 쿠폰 정책 조회", description = "현재 시스템에 등록된 모든 쿠폰 정책 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "쿠폰 정책 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = CouponPolicyResponseDto.class)))
    @GetMapping("/policy")
    ResponseEntity<List<CouponPolicyResponseDto>> getAllCouponPolicies();

    @Operation(summary = "특정 쿠폰 정책 조회", description = "지정된 ID의 쿠폰 정책 상세 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "쿠폰 정책 조회 성공",
            content = @Content(schema = @Schema(implementation = CouponPolicy.class)))
    @ApiResponse(responseCode = "404", description = "쿠폰 정책을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "Not Found",
                            value = "{\"errorCode\": \"NOT_FOUND\", \"errorMessage\": \"쿠폰 정책을 찾을 수 없습니다: policyId=1\"}"
                    )))
    @GetMapping("/policy/{policyId}")
    ResponseEntity<CouponPolicy> getCouponPolicy(
            @Parameter(description = "조회할 쿠폰 정책 ID", example = "1") @PathVariable Long policyId);

    @Operation(summary = "사용자에게 쿠폰 발급", description = "지정된 쿠폰 정책에 따라 사용자에게 쿠폰을 발급합니다.")
    @ApiResponse(responseCode = "201", description = "쿠폰 발급 성공",
            content = @Content(schema = @Schema(implementation = UserCouponResponseDto.class)))
    @ApiResponse(responseCode = "404", description = "쿠폰 정책을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "Policy Not Found",
                            value = "{\"errorCode\": \"NOT_FOUND\", \"errorMessage\": \"쿠폰 정책을 찾을 수 없습니다: couponPolicyId=1\"}"
                    )))
    @ApiResponse(responseCode = "409", description = "만료된 쿠폰 정책 또는 이미 발급된 쿠폰",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "Expired Policy",
                                    value = "{\"errorCode\": \"CONFLICT\", \"errorMessage\": \"만료된 쿠폰 정책입니다.\"}"
                            ),
                            @ExampleObject(name = "Coupon Already Issued",
                                    value = "{\"errorCode\": \"CONFLICT\", \"errorMessage\": \"이미 발급된 쿠폰입니다.\"}"
                            )
                    }))
    @PostMapping("/users/{userNo}/issue/{couponPolicyId}")
    ResponseEntity<UserCouponResponseDto> issueCouponToUser(
            @Parameter(description = "쿠폰을 발급받을 사용자 번호", example = "1") @PathVariable Long userNo,
            @Parameter(description = "발급할 쿠폰 정책 ID", example = "1") @PathVariable Long couponPolicyId);

    @Operation(summary = "웰컴 쿠폰 발급", description = "신규 가입 사용자에게 웰컴 쿠폰을 발급합니다. 이미 발급된 경우 예외 발생.")
    @ApiResponse(responseCode = "201", description = "웰컴 쿠폰 발급 성공",
            content = @Content(schema = @Schema(implementation = UserCouponResponseDto.class)))
    @ApiResponse(responseCode = "404", description = "웰컴 쿠폰 정책을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "Policy Not Found",
                            value = "{\"errorCode\": \"NOT_FOUND\", \"errorMessage\": \"웰컴 쿠폰 정책을 찾을 수 없습니다.\"}"
                    )))
    @ApiResponse(responseCode = "409", description = "이미 웰컴 쿠폰이 발급된 사용자",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "Already Issued",
                            value = "{\"errorCode\": \"CONFLICT\", \"errorMessage\": \"이미 웰컴 쿠폰이 발급된 사용자입니다.\"}"
                    )))
    @PostMapping("/users/{userNo}/issue-welcome")
    ResponseEntity<UserCouponResponseDto> issueWelcomeCoupon(
            @Parameter(description = "웰컴 쿠폰을 발급받을 사용자 번호", example = "1") @PathVariable Long userNo);

    @Operation(summary = "생일 쿠폰 발급", description = "사용자에게 생일 쿠폰을 발급합니다. 해당 연도에 이미 발급된 경우 예외 발생.")
    @ApiResponse(responseCode = "201", description = "생일 쿠폰 발급 성공",
            content = @Content(schema = @Schema(implementation = UserCouponResponseDto.class)))
    @ApiResponse(responseCode = "404", description = "생일 쿠폰 정책을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "Policy Not Found",
                            value = "{\"errorCode\": \"NOT_FOUND\", \"errorMessage\": \"생일 쿠폰 정책을 찾을 수 없습니다.\"}"
                    )))
    @ApiResponse(responseCode = "409", description = "이미 이번 연도 생일 쿠폰이 발급된 사용자",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "Already Issued This Year",
                            value = "{\"errorCode\": \"CONFLICT\", \"errorMessage\": \"이번 연도 생일 쿠폰이 이미 발급되었습니다.\"}"
                    )))
    @PostMapping("/users/{userNo}/issue-birthday")
    ResponseEntity<UserCouponResponseDto> issueBirthdayCoupon(
            @Parameter(description = "생일 쿠폰을 발급받을 사용자 번호", example = "1") @PathVariable Long userNo,
            @Parameter(description = "사용자 생일의 월 (1-12)", example = "7") @RequestParam int birthMonth);

    @Operation(summary = "도서 쿠폰 발급", description = "특정 도서 ID와 관련된 사용자 쿠폰을 발급합니다.")
    @ApiResponse(responseCode = "201", description = "도서 쿠폰 발급 성공",
            content = @Content(schema = @Schema(implementation = UserCouponResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "유효하지 않은 요청 데이터",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "Invalid Request",
                            value = "{\"errorCode\": \"BAD_REQUEST\", \"errorMessage\": \"필수 값이 누락되었거나 형식이 올바르지 않습니다.\"}"
                    )))
    @ApiResponse(responseCode = "404", description = "쿠폰 정책 또는 도서를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "Policy Not Found",
                                    value = "{\"errorCode\": \"NOT_FOUND\", \"errorMessage\": \"쿠폰 정책을 찾을 수 없습니다.\"}"
                            ),
                            @ExampleObject(name = "Book Not Found",
                                    value = "{\"errorCode\": \"NOT_FOUND\", \"errorMessage\": \"도서를 찾을 수 없습니다.\"}"
                            )
                    }))
    @PostMapping("/issue/book")
    ResponseEntity<UserCouponResponseDto> issueBookCoupon(
            @RequestBody(description = "도서 쿠폰 발급 요청 정보", required = true)
            IssueBookCouponRequestDto request);

    @Operation(summary = "사용자 보유 사용 가능한 쿠폰 조회", description = "사용자가 현재 사용할 수 있는 활성 쿠폰 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "활성 쿠폰 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = UserCouponResponseDto.class)))
    @GetMapping("/users/{userNo}/active")
    ResponseEntity<List<UserCouponResponseDto>> getActiveUserCoupons(
            @Parameter(description = "활성 쿠폰을 조회할 사용자 번호", example = "1") @PathVariable Long userNo);

    @Operation(summary = "사용자 사용 완료 쿠폰 조회", description = "사용자가 이미 사용한 쿠폰 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "사용 완료된 쿠폰 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = UserCouponResponseDto.class)))
    @GetMapping("/users/{userNo}/used")
    ResponseEntity<List<UserCouponResponseDto>> getUsedUserCoupons(
            @Parameter(description = "사용 완료된 쿠폰을 조회할 사용자 번호", example = "1") @PathVariable Long userNo);

    @Operation(summary = "쿠폰 사용 처리", description = "사용자가 주문 시 특정 쿠폰을 사용 처리합니다.")
    @ApiResponse(responseCode = "200", description = "쿠폰 사용 성공",
            content = @Content(schema = @Schema(implementation = String.class), examples = @ExampleObject(value = "쿠폰 사용 처리 완료.")))
    @ApiResponse(responseCode = "404", description = "쿠폰을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "Coupon Not Found",
                            value = "{\"errorCode\": \"NOT_FOUND\", \"errorMessage\": \"사용자 쿠폰을 찾을 수 없습니다: userCouponId=4\"}"
                    )))
    @ApiResponse(responseCode = "409", description = "이미 사용되었거나 만료된 쿠폰",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "Already Used",
                                    value = "{\"errorCode\": \"CONFLICT\", \"errorMessage\": \"이미 사용된 쿠폰입니다.\"}"
                            ),
                            @ExampleObject(name = "Expired",
                                    value = "{\"errorCode\": \"CONFLICT\", \"errorMessage\": \"만료된 쿠폰입니다.\"}"
                            ),
                            @ExampleObject(name = "Inactive",
                                    value = "{\"errorCode\": \"CONFLICT\", \"errorMessage\": \"활성 상태가 아닌 쿠폰입니다.\"}"
                            )
                    }))
    @PostMapping("/users/{userNo}/use/{userCouponId}")
    ResponseEntity<String> useCoupon(
            @Parameter(description = "쿠폰을 사용하는 사용자 번호", example = "1") @PathVariable Long userNo,
            @Parameter(description = "사용할 사용자 쿠폰 ID", example = "4") @PathVariable Long userCouponId,
            @RequestBody(description = "쿠폰 사용에 연결될 주문 정보", required = true)
            CouponUseRequestDto request);

    @Operation(summary = "할인 금액 계산", description = "사용자의 특정 쿠폰에 대한 예상 할인 금액을 계산합니다. 주문 금액 및 포함된 도서/카테고리 정보를 기반으로 합니다.")
    @ApiResponse(responseCode = "200", description = "할인 금액 계산 성공",
            content = @Content(schema = @Schema(type = "integer", format = "int32"), examples = @ExampleObject(value = "5000")))
    @ApiResponse(responseCode = "404", description = "쿠폰을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "Coupon Not Found",
                            value = "{\"errorCode\": \"NOT_FOUND\", \"errorMessage\": \"사용자 쿠폰을 찾을 수 없습니다: userCouponId=4\"}"
                    )))
    @ApiResponse(responseCode = "400", description = "쿠폰 적용 불가능 (최소 주문 금액 미달, 적용 대상 불일치 등)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "Minimum Order Amount Not Met",
                                    value = "{\"errorCode\": \"BAD_REQUEST\", \"errorMessage\": \"최소 주문 금액이 충족되지 않습니다.\"}"
                            ),
                            @ExampleObject(name = "Applicability Mismatch",
                                    value = "{\"errorCode\": \"BAD_REQUEST\", \"errorMessage\": \"쿠폰 적용 대상 도서/카테고리가 일치하지 않습니다.\"}"
                            )
                    }))
    @ApiResponse(responseCode = "409", description = "만료되었거나 활성 상태가 아닌 쿠폰",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "Expired",
                                    value = "{\"errorCode\": \"CONFLICT\", \"errorMessage\": \"만료된 쿠폰입니다.\"}"
                            ),
                            @ExampleObject(name = "Inactive",
                                    value = "{\"errorCode\": \"CONFLICT\", \"errorMessage\": \"활성 상태가 아닌 쿠폰입니다.\"}"
                            )
                    }))
    @GetMapping("/users/{userNo}/calculate-discount/{userCouponId}")
    ResponseEntity<Integer> calculateDiscount(
            @Parameter(description = "쿠폰을 적용하는 사용자 번호", example = "1") @PathVariable Long userNo,
            @Parameter(description = "계산에 사용할 사용자 쿠폰 ID", example = "4") @PathVariable Long userCouponId,
            @Parameter(description = "총 주문 금액", example = "30000", required = true) @RequestParam int orderAmount,
            @Parameter(description = "주문에 포함된 도서 ID 목록 (선택 사항, 콤마로 구분)", example = "10,11") @RequestParam(required = false) List<Long> bookIds,
            @Parameter(description = "주문에 포함된 카테고리 ID 목록 (선택 사항, 콤마로 구분)", example = "101,102") @RequestParam(required = false) List<Long> categoryIds);
}