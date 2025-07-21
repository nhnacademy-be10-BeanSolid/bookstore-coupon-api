package com.nhnacademy.listener;

import com.nhnacademy.domain.UserBirthEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BirthdayEventListener {

    private final CouponService couponService;

    @RabbitListener(queues = "birthday-queue")
    public void handleBirthdayEvent(UserBirthEvent event) {
        log.info("Received birthday event for user: {}", event.getUserNo());
        try {
            couponService.issueBirthdayCoupon(event.getUserNo(), event.getUserBirth());
            log.info("Successfully issued birthday coupon for user: {}", event.getUserNo());
        } catch (Exception e) {
            log.error("Failed to issue birthday coupon for user {}: {}", event.getUserNo(), e.getMessage());
        }
    }
}
