package com.nhnacademy.listener;

import com.nhnacademy.config.RabbitMQConfig;
import com.nhnacademy.controller.dto.IssueCouponsToUsersRequest;
import com.nhnacademy.domain.CouponPolicy;
import com.nhnacademy.domain.UsedCoupon;
import com.nhnacademy.domain.UserCouponStatus;
import com.nhnacademy.exception.CouponNotFoundException;
import com.nhnacademy.repository.CouponPolicyRepository;
import com.nhnacademy.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponIssueListener {

    private final CouponPolicyRepository couponPolicyRepository;
    private final UserCouponRepository userCouponRepository;

    @RabbitListener(queues = RabbitMQConfig.ISSUE_COUPONS_TO_USERS_QUEUE)
    @Transactional
    public void handleIssueCouponsToUsersRequest(IssueCouponsToUsersRequest request) {
        log.info("Received request to issue couponPolicyId: {} to users: {}", request.couponPolicyId(), request.userNos().size());

        CouponPolicy couponPolicy = couponPolicyRepository.findById(request.couponPolicyId())
                .orElseThrow(() -> new CouponNotFoundException("Coupon policy not found for ID: " + request.couponPolicyId()));

        List<UsedCoupon> userCouponsToSave = request.userNos().stream()
                .map(userNo -> {
                    LocalDateTime userCouponExpiredAt;
                    if (couponPolicy.getCouponIssuePeriod() != null) {
                        userCouponExpiredAt = LocalDateTime.now().plusDays(couponPolicy.getCouponIssuePeriod());
                    } else if (couponPolicy.getCouponExpiredAt() != null) {
                        userCouponExpiredAt = couponPolicy.getCouponExpiredAt();
                    } else {
                        userCouponExpiredAt = LocalDateTime.now().plusDays(365); // Default 1 year if no period or fixed expiry
                    }

                    return UsedCoupon.builder()
                            .userNo(userNo)
                            .couponPolicy(couponPolicy)
                            .issuedAt(LocalDateTime.now())
                            .expiredAt(userCouponExpiredAt)
                            .status(UserCouponStatus.ACTIVE)
                            .build();
                })
                .collect(Collectors.toList());

        userCouponRepository.saveAll(userCouponsToSave);
        log.info("Successfully issued couponPolicyId: {} to {} users.", request.couponPolicyId(), request.userNos().size());
    }
}
