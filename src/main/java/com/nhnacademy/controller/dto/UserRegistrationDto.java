package com.nhnacademy.controller.dto;

import com.nhnacademy.domain.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDto {
    private String userId;
    private String userName;
    private LocalDate uesrBirth;

    public Users toEntity() {
        return Users.builder()
                .userId(userId)
                .userName(userName)
                .userBirth(uesrBirth)
                .build();
    }
}
