package com.nhnacademy.controller.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class UserBirthdayDtoTest {

    @Test
    @DisplayName("UserBirthdayDto: NoArgsConstructor 및 Setter 테스트")
    void testNoArgsConstructorAndSetter() {
        UserBirthdayDto dto = new UserBirthdayDto();
        String userNo = "testUser123";
        LocalDate userBirth = LocalDate.of(1990, 5, 15);

        dto.setUserNo(userNo);
        dto.setUserBirth(userBirth);

        assertThat(dto.getUserNo()).isEqualTo(userNo);
        assertThat(dto.getUserBirth()).isEqualTo(userBirth);
    }

    @Test
    @DisplayName("UserBirthdayDto: AllArgsConstructor 및 Getter 테스트")
    void testAllArgsConstructorAndGetter() {
        String userNo = "anotherUser456";
        LocalDate userBirth = LocalDate.of(1988, 11, 22);
        UserBirthdayDto dto = new UserBirthdayDto(userNo, userBirth);

        assertThat(dto.getUserNo()).isEqualTo(userNo);
        assertThat(dto.getUserBirth()).isEqualTo(userBirth);
    }
}
