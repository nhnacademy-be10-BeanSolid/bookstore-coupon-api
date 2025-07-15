package com.nhnacademy.service.impl;

import com.nhnacademy.controller.dto.UserResponse;
import com.nhnacademy.service.ExternalUserService;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExternalUserServiceImpl implements ExternalUserService {

    @Override
    public List<UserResponse> getBirthdayUsersByMonth(int month) {
        List<UserResponse> birthdayUsers = new ArrayList<>();

        LocalDate today = LocalDate.now();
        if (today.getMonthValue() == month) {
            birthdayUsers.add(new UserResponse(1L, "user1", "Test User 1", "user1@example.com", LocalDate.of(1990, month, 15)));
            birthdayUsers.add(new UserResponse(2L, "user2", "Test User 2", "user2@example.com", LocalDate.of(1992, month, 20)));
        }
        return birthdayUsers;
    }

    @Override
    public UserResponse getUser(String userNo) {
        // Simulate API call to user-api
        return new UserResponse(Long.parseLong(userNo), "testUser", "Test User", "test@example.com", LocalDate.now());
    }
}