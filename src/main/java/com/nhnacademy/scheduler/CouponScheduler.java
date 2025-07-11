package com.nhnacademy.scheduler;

import com.nhnacademy.service.CouponService;
import com.nhnacademy.service.ExternalUserService;
import com.nhnacademy.controller.dto.UserBirthdayDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponScheduler {

    private final ExternalUserService externalUserService;
    private final CouponService couponService;

    @Scheduled(cron = "0 0 0 1 * *")
    public void issueBirthdayCoupons() {
        log.info("생일 쿠폰 발급 스케줄러 시작: {}", LocalDateTime.now());

        int currentMonth = LocalDate.now().getMonthValue();
        List<UserBirthdayDto> birthdayUsers = externalUserService.getBirthdayUsersByMonth(currentMonth);

        if (birthdayUsers.isEmpty()) {
            log.info("이번 달 생일인 사용자가 없습니다.");
            return;
        }

        for (UserBirthdayDto user : birthdayUsers) {
            try {
                couponService.issueBirthdayCoupon(user.getUserNo(), user.getUserBirth());
                log.info("사용자 ID {} 에게 생일 쿠폰이 성공적으로 발급되었습니다.", user.getUserNo());
            } catch (IllegalStateException e) {
                log.warn("사용자 ID {} 에게 이미 이번 연도 생일 쿠폰이 발급되었습니다: {}", user.getUserNo(), e.getMessage());
            } catch (Exception e) {
                log.error("사용자 ID {} 에게 생일 쿠폰 발급 중 오류 발생: {}", user.getUserNo(), e.getMessage(), e);
            }
        }
        log.info("생일 쿠폰 발급 스케줄러 종료.");
    }
}