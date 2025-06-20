package com.nhnacademy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EntityScan(basePackages = {"com.nhnacademy.domain"})
public class BookstoreCouponApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookstoreCouponApiApplication.class, args);
    }
}