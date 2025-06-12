package com.nhnacademy.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Users {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_birth")
    private LocalDate userBirth;

    @Column(name = "user_name", length = 50)
    private String userName;
}