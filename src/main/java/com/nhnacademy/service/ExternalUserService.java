package com.nhnacademy.service;

import com.nhnacademy.dto.response.UserResponseDto;
import java.util.List;

public interface ExternalUserService {
    List<UserResponseDto> getBirthdayUsersByMonth(int month);
    UserResponseDto getUser(String userNo);
}