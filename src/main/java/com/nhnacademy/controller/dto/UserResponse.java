package com.nhnacademy.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long userNo;
    private String userId;
    private String userName;
    private String userEmail;
    private LocalDate userBirth;
}