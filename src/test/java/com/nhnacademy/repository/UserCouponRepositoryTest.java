package com.nhnacademy.repository;

import com.nhnacademy.domain.UserCoupon;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class UserCouponRepositoryTest {

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Test
    void testSave() {
        UserCoupon userCoupon = new UserCoupon();
        UserCoupon savedUserCoupon = userCouponRepository.save(userCoupon);
        assertNotNull(savedUserCoupon.getUserCouponId());
    }
}
