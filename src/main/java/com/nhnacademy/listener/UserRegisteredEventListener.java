package com.nhnacademy.listener;

import com.nhnacademy.config.RabbitMQConfig;
import com.nhnacademy.event.UserRegisteredEvent;
import com.nhnacademy.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegisteredEventListener {

    private final CouponService couponService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        log.info("Received user registered event for userNo: {}", event.getUserNo());
        try {
            couponService.issueWelcomeCoupon(String.valueOf(event.getUserNo()));
            log.info("Welcome coupon issued successfully for userNo: {}", event.getUserNo());
        } catch (Exception e) {
            log.error("Failed to issue welcome coupon for userNo {}: {}", event.getUserNo(), e.getMessage());
        }
    }
}
