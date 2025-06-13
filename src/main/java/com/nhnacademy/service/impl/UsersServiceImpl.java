package com.nhnacademy.service.impl;

import com.nhnacademy.repository.UsersRepository;
import com.nhnacademy.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsersServiceImpl implements UsersService {
    private final UsersRepository usersRepository;

    @Override
    public List<String> getUserIdsByBirthMonth(int month) {
        return usersRepository.findUserIdsByBirthMonth(month);
    }
}
