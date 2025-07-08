package com.nhnacademy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class BookstoreCouponApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookstoreCouponApiApplication.class, args);
    }
}