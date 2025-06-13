package com.nhnacademy.repository;

import com.nhnacademy.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, String> {
    @Query("SELECT u.userId FROM Users u WHERE MONTH(u.userBirth) = :month")
    List<String> findUserIdsByBirthMonth(@Param("month") int month);

    Optional<Users> findByUserId(String userId);
}