package com.nhnacademy.service;

import com.nhnacademy.common.exception.CouponAlreadyExistException;
import com.nhnacademy.domain.*;
import com.nhnacademy.exception.*;
import com.nhnacademy.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CouponServiceTest {

    private CouponPolicyRepository couponPolicyRepository;
    private UserCouponRepository userCouponRepository;
    private CouponBookRepository couponBookRepository;
    private CouponCategoryRepository couponCategoryRepository;

    private CouponService couponService;

    @BeforeEach
    void setUp() {
        couponPolicyRepository = mock(CouponPolicyRepository.class);
        userCouponRepository = mock(UserCouponRepository.class);
        couponBookRepository = mock(CouponBookRepository.class);
        couponCategoryRepository = mock(CouponCategoryRepository.class);

        couponService = new CouponService(
                couponPolicyRepository,
                userCouponRepository,
                couponBookRepository,
                couponCategoryRepository
        );
    }

    @Test
    void createCouponPolicy_withBookScope_savesBooks() {
        CouponPolicy policy = createPolicy(1L, CouponScope.BOOK, CouponType.GENERAL);
        when(couponPolicyRepository.save(any())).thenReturn(policy);

        couponService.createCouponPolicy("Book Coupon", CouponDiscountType.AMOUNT, 1000,
                5000, null, CouponScope.BOOK, LocalDateTime.now().plusDays(10), 10,
                List.of(1L, 2L), null, CouponType.GENERAL);

        verify(couponBookRepository, times(2)).save(any());
    }

    @Test
    void createCouponPolicy_withCategoryScope_savesCategories() {
        CouponPolicy policy = createPolicy(2L, CouponScope.CATEGORY, CouponType.GENERAL);
        when(couponPolicyRepository.save(any())).thenReturn(policy);

        couponService.createCouponPolicy("Cat Coupon", CouponDiscountType.AMOUNT, 1000,
                null, null, CouponScope.CATEGORY, LocalDateTime.now().plusDays(10), 10,
                null, List.of(3L, 4L), CouponType.GENERAL);

        verify(couponCategoryRepository, times(2)).save(any());
    }

    @Test
    void getAllCouponPolicies_returnsList() {
        when(couponPolicyRepository.findAll()).thenReturn(List.of(createPolicy(1L, CouponScope.ALL, CouponType.GENERAL)));

        assertThat(couponService.getAllCouponPolicies()).hasSize(1);
    }

    @Test
    void getCouponPolicyById_returnsPresent() {
        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.of(createPolicy(1L, CouponScope.ALL, CouponType.GENERAL)));

        assertThat(couponService.getCouponPolicyById(1L)).isPresent();
    }

    @Test
    void issueCouponToUser_withExpiredPolicy_throws() {
        CouponPolicy policy = createPolicy(1L, CouponScope.ALL, CouponType.GENERAL);
        policy.setCouponExpiredAt(LocalDateTime.now().minusDays(1));
        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.of(policy));

        assertThatThrownBy(() -> couponService.issueCouponToUser("user1", 1L))
                .isInstanceOf(CouponExpiredException.class);
    }

    @Test
    void issueCouponToUser_withValidPolicy_issuesCoupon() {
        CouponPolicy policy = createPolicy(2L, CouponScope.ALL, CouponType.GENERAL);
        policy.setCouponIssuePeriod(7);
        when(couponPolicyRepository.findById(2L)).thenReturn(Optional.of(policy));
        when(userCouponRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UsedCoupon result = couponService.issueCouponToUser("user123", 2L);
        assertThat(result.getStatus()).isEqualTo(UserCouponStatus.ACTIVE);
    }

    @Test
    void getActiveUserCoupons_returnsList() {
        when(userCouponRepository.findActiveCouponsByUserIdAndPeriod(any(), any(), any()))
                .thenReturn(List.of(mock(UsedCoupon.class)));

        assertThat(couponService.getActiveUserCoupons("abc")).hasSize(1);
    }

    @Test
    void getUsedUserCoupons_returnsList() {
        when(userCouponRepository.findUsedCouponsByUserId("abc")).thenReturn(List.of(mock(UsedCoupon.class)));

        assertThat(couponService.getUsedUserCoupons("abc")).hasSize(1);
    }

    @Test
    void getExpiredUserCoupons_returnsList() {
        when(userCouponRepository.findExpiredCouponsByUserId("abc")).thenReturn(List.of(mock(UsedCoupon.class)));

        assertThat(couponService.getExpiredUserCoupons("abc")).hasSize(1);
    }

    @Test
    void getCouponPolicy_existingId_returnsPolicy() {
        when(couponPolicyRepository.findById(5L)).thenReturn(Optional.of(createPolicy(5L, CouponScope.ALL, CouponType.GENERAL)));

        assertThat(couponService.getCouponPolicy(5L)).isNotNull();
    }

    @Test
    void getCouponPolicy_nonexistent_throws() {
        when(couponPolicyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.getCouponPolicy(99L))
                .isInstanceOf(CouponNotFoundException.class);
    }

    @Test
    void issueWelcomeCoupon_success() {
        CouponPolicy welcome = createPolicy(100L, CouponScope.ALL, CouponType.WELCOME);
        when(couponPolicyRepository.findByCouponType(CouponType.WELCOME)).thenReturn(Optional.of(welcome));
        when(couponPolicyRepository.findById(100L)).thenReturn(Optional.of(welcome));
        when(userCouponRepository.findByUserNoAndCouponPolicy("user1", welcome)).thenReturn(List.of());
        when(userCouponRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UsedCoupon result = couponService.issueWelcomeCoupon("user1");
        assertThat(result.getCouponPolicy().getCouponId()).isEqualTo(100L);
    }

    @Test
    void issueWelcomeCoupon_alreadyIssued_throws() {
        CouponPolicy welcome = createPolicy(100L, CouponScope.ALL, CouponType.WELCOME);
        when(couponPolicyRepository.findByCouponType(CouponType.WELCOME)).thenReturn(Optional.of(welcome));
        when(userCouponRepository.findByUserNoAndCouponPolicy("user1", welcome))
                .thenReturn(List.of(mock(UsedCoupon.class)));

        assertThatThrownBy(() -> couponService.issueWelcomeCoupon("user1"))
                .isInstanceOf(CouponAlreadyExistException.class);
    }

    @Test
    void issueBirthdayCoupon_success() {
        CouponPolicy birthday = createPolicy(200L, CouponScope.ALL, CouponType.BIRTHDAY);
        when(couponPolicyRepository.findByCouponType(CouponType.BIRTHDAY)).thenReturn(Optional.of(birthday));
        when(userCouponRepository.findByUserNoAndCouponPolicy("user2", birthday)).thenReturn(List.of());
        when(userCouponRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UsedCoupon result = couponService.issueBirthdayCoupon("user2", LocalDate.of(1995, 12, 5));
        assertThat(result.getCouponPolicy().getCouponId()).isEqualTo(200L);
    }

    @Test
    void issueBirthdayCoupon_alreadyIssuedThisYear_throws() {
        CouponPolicy birthday = createPolicy(200L, CouponScope.ALL, CouponType.BIRTHDAY);
        UsedCoupon prev = UsedCoupon.builder()
                .issuedAt(LocalDateTime.now().withMonth(1))
                .build();
        when(couponPolicyRepository.findByCouponType(CouponType.BIRTHDAY)).thenReturn(Optional.of(birthday));
        when(userCouponRepository.findByUserNoAndCouponPolicy("user3", birthday)).thenReturn(List.of(prev));

        assertThatThrownBy(() -> couponService.issueBirthdayCoupon("user3", LocalDate.of(1990, 3, 3)))
                .isInstanceOf(CouponAlreadyExistException.class);
    }

    @Test
    void useCoupon_successful() {
        UsedCoupon coupon = UsedCoupon.builder()
                .userNo("u1")
                .couponPolicy(mock(CouponPolicy.class))
                .orderId(null)
                .issuedAt(LocalDateTime.now().minusDays(1))
                .expiredAt(LocalDateTime.now().plusDays(1))
                .usedAt(null)
                .status(UserCouponStatus.ACTIVE)
                .build();

        when(userCouponRepository.findByUserNoAndUserCouponId("u1", coupon.getUserCouponId()))
                .thenReturn(Optional.of(coupon));

        couponService.useCoupon("u1", coupon.getUserCouponId(), 2000L);

        assertThat(coupon.getStatus()).isEqualTo(UserCouponStatus.USED);
    }


    @Test
    void useCoupon_alreadyUsed_throws() {
        UsedCoupon used = UsedCoupon.builder()
                .userNo("u1")
                .couponPolicy(mock(CouponPolicy.class))
                .orderId(123L)
                .issuedAt(LocalDateTime.now().minusDays(2))
                .expiredAt(LocalDateTime.now().plusDays(1))
                .usedAt(LocalDateTime.now().minusDays(1))
                .status(UserCouponStatus.USED)
                .build();

        when(userCouponRepository.findByUserNoAndUserCouponId("u1", used.getUserCouponId()))
                .thenReturn(Optional.of(used));

        assertThatThrownBy(() -> couponService.useCoupon("u1", used.getUserCouponId(), 2000L))
                .isInstanceOf(CouponAlreadyUsedException.class);
    }


    @Test
    void calculateDiscountAmount_percentSuccess() {
        CouponPolicy policy = CouponPolicy.builder()
                .couponId(9L)
                .couponDiscountType(CouponDiscountType.PERCENT)
                .couponDiscountAmount(10)
                .couponScope(CouponScope.ALL)
                .couponType(CouponType.GENERAL)
                .build();

        UsedCoupon coupon = UsedCoupon.builder()
                .userNo("user123")
                .couponPolicy(policy)
                .issuedAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusDays(7))
                .status(UserCouponStatus.ACTIVE)
                .build();

        when(userCouponRepository.findByUserNoAndUserCouponId("u1", 12L)).thenReturn(Optional.of(coupon));

        int discount = couponService.calculateDiscountAmount("u1", 12L, 10000, List.of(), List.of());
        assertThat(discount).isEqualTo(1000);
    }

    @Test
    void calculateDiscountAmount_invalidScope_throws() {
        CouponPolicy policy = CouponPolicy.builder()
                .couponId(1L)
                .couponDiscountType(null)
                .couponScope(CouponScope.ALL)
                .couponType(CouponType.GENERAL)
                .build();

        UsedCoupon coupon = UsedCoupon.builder()
                .userNo("user123")
                .couponPolicy(policy)
                .issuedAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusDays(7))
                .status(UserCouponStatus.ACTIVE)
                .build();

        when(userCouponRepository.findByUserNoAndUserCouponId("u1", 99L)).thenReturn(Optional.of(coupon));

        assertThatThrownBy(() -> couponService.calculateDiscountAmount("u1", 99L, 10000, List.of(), List.of()))
                .isInstanceOf(CouponNotApplicableException.class);
    }

    private CouponPolicy createPolicy(Long id, CouponScope scope, CouponType type) {
        return CouponPolicy.builder()
                .couponId(id)
                .couponName("Test")
                .couponScope(scope)
                .couponDiscountAmount(1000)
                .couponDiscountType(CouponDiscountType.AMOUNT)
                .couponExpiredAt(LocalDateTime.now().plusDays(1))
                .couponType(type)
                .build();
    }
}
