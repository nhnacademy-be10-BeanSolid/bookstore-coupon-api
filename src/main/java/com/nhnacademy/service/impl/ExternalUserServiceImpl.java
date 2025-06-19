package com.nhnacademy.service.impl;

import com.nhnacademy.service.ExternalUserService;
import com.nhnacademy.controller.dto.UserBirthdayDto;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExternalUserServiceImpl implements ExternalUserService {

    @Override
    public List<UserBirthdayDto> getBirthdayUsersByMonth(int month) {
        List<UserBirthdayDto> birthdayUsers = new ArrayList<>();

        LocalDate today = LocalDate.now();
        if (today.getMonthValue() == month) {
            birthdayUsers.add(new UserBirthdayDto("user1", LocalDate.of(1990, month, 15)));
            birthdayUsers.add(new UserBirthdayDto("user2", LocalDate.of(1992, month, 20)));
        }
        return birthdayUsers;
    }
}