package com.nhnacademy.service.impl;

import com.nhnacademy.dto.response.UserResponseDto;
import com.nhnacademy.service.ExternalUserService;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExternalUserServiceImpl implements ExternalUserService {

    @Override
    public List<UserResponseDto> getBirthdayUsersByMonth(int month) {
        List<UserResponseDto> birthdayUsers = new ArrayList<>();

        LocalDate today = LocalDate.now();
        if (today.getMonthValue() == month) {
            birthdayUsers.add(new UserResponseDto(1L, "user1", "Test User 1", "user1@example.com", LocalDate.of(1990, month, 15)));
            birthdayUsers.add(new UserResponseDto(2L, "user2", "Test User 2", "user2@example.com", LocalDate.of(1992, month, 20)));
        }
        return birthdayUsers;
    }

    @Override
    public UserResponseDto getUser(String userNo) {
        // Simulate API call to user-api
        return new UserResponseDto(Long.parseLong(userNo), "testUser", "Test User", "test@example.com", LocalDate.now());
    }
}