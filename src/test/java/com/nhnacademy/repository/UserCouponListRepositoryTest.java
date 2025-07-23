package com.nhnacademy.repository;

import com.nhnacademy.domain.CouponPolicy;
import com.nhnacademy.domain.UserCouponList;
import com.nhnacademy.domain.enumtype.CouponDiscountType;
import com.nhnacademy.domain.enumtype.CouponScope;
import com.nhnacademy.domain.enumtype.CouponType;
import com.nhnacademy.domain.enumtype.UserCouponStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserCouponListRepositoryTest {

    @Autowired
    private UserCouponListRepository userCouponListRepository;

    @Autowired
    private CouponPolicyRepository couponPolicyRepository;

    private CouponPolicy savedPolicy;

    @BeforeEach
    void setUp() {
        CouponPolicy policy = CouponPolicy.builder()
                .couponName("Test Policy")
                .couponType(CouponType.GENERAL)
                .couponDiscountType(CouponDiscountType.PERCENT)
                .couponDiscountAmount(0)
                .couponMinimumOrderAmount(0)
                .couponMaximumDiscountAmount(0)
                .couponScope(CouponScope.ALL)
                .couponExpiredAt(LocalDateTime.now().plusDays(10))
                .couponIssuePeriod(300)
                .build();
        savedPolicy = couponPolicyRepository.save(policy);
    }

    @Test
    @DisplayName("findByUserNoAndCouponPolicy - 정상 조회")
    void testFindByUserNoAndCouponPolicy() {

        UserCouponList userCoupon = UserCouponList.builder()
                .userNo(1L)
                .couponPolicy(savedPolicy)
                .status(UserCouponStatus.ACTIVE)
                .expiredAt(LocalDateTime.now().plusDays(5))
                .build();
        userCouponListRepository.save(userCoupon);

        List<UserCouponList> list = userCouponListRepository.findByUserNoAndCouponPolicy(1L, savedPolicy);

        assertThat(list).isNotEmpty();
        assertThat(list.getFirst().getUserNo()).isEqualTo(1L);
        assertThat(list.getFirst().getCouponPolicy().getCouponId()).isEqualTo(savedPolicy.getCouponId());
    }

    @Test
    @DisplayName("findByUserNoAndUserCouponId - 정상 조회")
    void testFindByUserNoAndUserCouponId() {

        UserCouponList userCoupon = UserCouponList.builder()
                .userNo(2L)
                .couponPolicy(savedPolicy)
                .status(UserCouponStatus.ACTIVE)
                .expiredAt(LocalDateTime.now().plusDays(5))
                .build();
        UserCouponList savedUserCoupon = userCouponListRepository.save(userCoupon);

        Optional<UserCouponList> found = userCouponListRepository.findByUserNoAndUserCouponId(2L, savedUserCoupon.getUserCouponId());

        assertThat(found).isPresent();
        assertThat(found.get().getUserNo()).isEqualTo(2L);
        assertThat(found.get().getUserCouponId()).isEqualTo(savedUserCoupon.getUserCouponId());
    }

    @Test
    @DisplayName("deleteByCouponPolicy - 쿠폰 정책 삭제에 따른 유저 쿠폰 삭제")
    void testDeleteByCouponPolicy() {

        UserCouponList userCoupon1 = UserCouponList.builder()
                .userNo(3L)
                .couponPolicy(savedPolicy)
                .status(UserCouponStatus.ACTIVE)
                .expiredAt(LocalDateTime.now().plusDays(5))
                .build();

        UserCouponList userCoupon2 = UserCouponList.builder()
                .userNo(4L)
                .couponPolicy(savedPolicy)
                .status(UserCouponStatus.EXPIRED)
                .expiredAt(LocalDateTime.now().minusDays(2))
                .build();

        userCouponListRepository.save(userCoupon1);
        userCouponListRepository.save(userCoupon2);

        userCouponListRepository.deleteByCouponPolicy(savedPolicy);

        List<UserCouponList> listUser3 = userCouponListRepository.findByUserNoAndCouponPolicy(3L, savedPolicy);
        assertThat(listUser3).isEmpty();

        List<UserCouponList> listUser4 = userCouponListRepository.findByUserNoAndCouponPolicy(4L, savedPolicy);
        assertThat(listUser4).isEmpty();
    }

    @Test
    @DisplayName("findByUserNoAndStatus - 특정 상태 쿠폰 조회")
    void testFindByUserNoAndStatus() {

        UserCouponList activeCoupon = UserCouponList.builder()
                .userNo(5L)
                .couponPolicy(savedPolicy)
                .status(UserCouponStatus.ACTIVE)
                .expiredAt(LocalDateTime.now().plusDays(4))
                .build();

        UserCouponList usedCoupon = UserCouponList.builder()
                .userNo(5L)
                .couponPolicy(savedPolicy)
                .status(UserCouponStatus.USED)
                .expiredAt(LocalDateTime.now().plusDays(1))
                .build();

        userCouponListRepository.save(activeCoupon);
        userCouponListRepository.save(usedCoupon);

        List<UserCouponList> activeList = userCouponListRepository.findByUserNoAndStatus(5L, UserCouponStatus.ACTIVE);
        assertThat(activeList).hasSize(1);
        assertThat(activeList.getFirst().getStatus()).isEqualTo(UserCouponStatus.ACTIVE);

        List<UserCouponList> usedList = userCouponListRepository.findByUserNoAndStatus(5L, UserCouponStatus.USED);
        assertThat(usedList).hasSize(1);
        assertThat(usedList.getFirst().getStatus()).isEqualTo(UserCouponStatus.USED);
    }

    @Test
    @DisplayName("findActiveCouponsByUserNo : 활성 상태이면서 만료되지 않은 쿠폰 조회")
    void testFindActiveCouponsByUserNo() {
        UserCouponList activeCoupon = UserCouponList.builder()
                .userNo(10L)
                .couponPolicy(savedPolicy)
                .status(UserCouponStatus.ACTIVE)
                .expiredAt(LocalDateTime.now().plusDays(1))
                .build();

        UserCouponList expiredButActiveStatusCoupon = UserCouponList.builder()
                .userNo(10L)
                .couponPolicy(savedPolicy)
                .status(UserCouponStatus.ACTIVE)
                .expiredAt(LocalDateTime.now().minusDays(1))
                .build();

        userCouponListRepository.save(activeCoupon);
        userCouponListRepository.save(expiredButActiveStatusCoupon);

        List<UserCouponList> results = userCouponListRepository.findActiveCouponsByUserNo(10L);

        assertThat(results)
                .isNotEmpty()
                .allMatch(c -> c.getStatus() == UserCouponStatus.ACTIVE)
                .allMatch(c -> c.getExpiredAt().isAfter(LocalDateTime.now()))
                .contains(activeCoupon)
                .doesNotContain(expiredButActiveStatusCoupon);

        assertThat(results)
                .extracting("expiredAt", LocalDateTime.class)
                .allMatch(expiredAt -> expiredAt.isAfter(LocalDateTime.now()));

    }

    @Test
    @DisplayName("findUsedCouponsByUserNo : 상태가 USED 인 쿠폰 조회")
    void testFindUsedCouponsByUserNo() {
        UserCouponList usedCoupon = UserCouponList.builder()
                .userNo(20L)
                .couponPolicy(savedPolicy)
                .status(UserCouponStatus.USED)
                .expiredAt(LocalDateTime.now().minusDays(1))
                .build();

        UserCouponList activeCoupon = UserCouponList.builder()
                .userNo(20L)
                .couponPolicy(savedPolicy)
                .status(UserCouponStatus.ACTIVE)
                .expiredAt(LocalDateTime.now().plusDays(5))
                .build();

        userCouponListRepository.save(usedCoupon);
        userCouponListRepository.save(activeCoupon);

        List<UserCouponList> results = userCouponListRepository.findUsedCouponsByUserNo(20L);

        assertThat(results).isNotEmpty()
                .allMatch(c -> c.getStatus() == UserCouponStatus.USED)
                .contains(usedCoupon)
                .doesNotContain(activeCoupon);
    }

    @Test
    @DisplayName("findExpiredCouponsByUserNo : 상태가 EXPIRED 인 쿠폰 조회")
    void testFindExpiredCouponsByUserNo() {
        UserCouponList expiredCoupon = UserCouponList.builder()
                .userNo(30L)
                .couponPolicy(savedPolicy)
                .status(UserCouponStatus.EXPIRED)
                .expiredAt(LocalDateTime.now().minusDays(10))
                .build();

        UserCouponList usedCoupon = UserCouponList.builder()
                .userNo(30L)
                .couponPolicy(savedPolicy)
                .status(UserCouponStatus.USED)
                .expiredAt(LocalDateTime.now().minusDays(5))
                .build();

        userCouponListRepository.save(expiredCoupon);
        userCouponListRepository.save(usedCoupon);

        List<UserCouponList> results = userCouponListRepository.findExpiredCouponsByUserNo(30L);

        assertThat(results).isNotEmpty()
                .allMatch(c -> c.getStatus() == UserCouponStatus.EXPIRED)
                .contains(expiredCoupon)
                .doesNotContain(usedCoupon);
    }

}
