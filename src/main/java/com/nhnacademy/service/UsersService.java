package com.nhnacademy.service;

import com.nhnacademy.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UsersService {
    List<String> getUserIdsByBirthMonth(int month);
}