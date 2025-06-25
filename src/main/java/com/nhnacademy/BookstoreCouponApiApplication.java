package com.nhnacademy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EntityScan(basePackages = {"com.nhnacademy.domain"})
@EnableDiscoveryClient
public class BookstoreCouponApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookstoreCouponApiApplication.class, args);
    }
}