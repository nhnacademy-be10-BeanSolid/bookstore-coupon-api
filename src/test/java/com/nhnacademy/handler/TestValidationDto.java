package com.nhnacademy.handler;

import jakarta.validation.constraints.NotBlank;

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
