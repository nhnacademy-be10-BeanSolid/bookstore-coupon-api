package com.nhnacademy.service;

import com.nhnacademy.controller.dto.UserBirthdayDto;
import java.util.List;

public interface ExternalUserService {
    List<UserBirthdayDto> getBirthdayUsersByMonth(int month);
}