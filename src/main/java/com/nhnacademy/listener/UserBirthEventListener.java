package com.nhnacademy.listener;

import com.nhnacademy.config.RabbitMQConfig;
import com.nhnacademy.event.UserBirthEvent;
import com.nhnacademy.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserBirthEventListener {

    private final CouponService couponService;

    @RabbitListener(queues = RabbitMQConfig.BIRTHDAY_QUEUE_NAME)
    public void handleUserBirthEvent(UserBirthEvent event) {
        log.info("Received user birth event for userNo: {} with birth date: {}", event.getUserNo(), event.getUserBirth());
        try {
            couponService.issueBirthdayCoupon(String.valueOf(event.getUserNo()), event.getUserBirth());
            log.info("Birthday coupon issued successfully for userNo: {}", event.getUserNo());
        } catch (IllegalStateException e) {
            log.warn("User ID {} already received birthday coupon this year: {}", event.getUserNo(), e.getMessage());
        } catch (Exception e) {
            log.error("Failed to issue birthday coupon for userNo {}: {}", event.getUserNo(), e.getMessage(), e);
        }
    }
}
