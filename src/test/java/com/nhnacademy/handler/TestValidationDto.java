package com.nhnacademy.handler;

import jakarta.validation.constraints.NotBlank;

// 유효성 검사 (테스트용)
public class TestValidationDto {

    @NotBlank(message = "필수 값입니다.")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}