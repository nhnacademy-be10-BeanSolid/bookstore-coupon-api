package com.nhnacademy.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ErrorResponse", description = "오류 응답 공통 스키마")
public class ErrorResponse {
    @Schema(description = "오류 코드", example = "BAD_REQUEST")
    private String errorCode;
    @Schema(description = "오류 메시지", example = "요청 값이 유효하지 않습니다.")
    private String errorMessage;
}