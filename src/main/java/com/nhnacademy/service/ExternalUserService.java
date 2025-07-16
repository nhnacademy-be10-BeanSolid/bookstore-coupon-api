package com.nhnacademy.service;

import com.nhnacademy.controller.dto.UserResponse;
import java.util.List;

public interface ExternalUserService {
    List<UserResponse> getBirthdayUsersByMonth(int month);
    UserResponse getUser(String userNo);
}