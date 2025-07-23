package com.nhnacademy.service.impl;

import com.nhnacademy.common.config.RabbitMQConfig;
import com.nhnacademy.common.exception.CouponAlreadyExistException;
import com.nhnacademy.domain.CouponBook;
import com.nhnacademy.domain.CouponCategory;
import com.nhnacademy.domain.CouponPolicy;
import com.nhnacademy.domain.UserCouponList;
import com.nhnacademy.domain.enumtype.CouponDiscountType;
import com.nhnacademy.domain.enumtype.CouponScope;
import com.nhnacademy.domain.enumtype.CouponType;
import com.nhnacademy.domain.enumtype.UserCouponStatus;
import com.nhnacademy.dto.request.CouponPolicyRequestDto;
import com.nhnacademy.dto.request.IssueBookCouponRequestDto;
import com.nhnacademy.exception.*;
import com.nhnacademy.repository.CouponBookRepository;
import com.nhnacademy.repository.CouponCategoryRepository;
import com.nhnacademy.repository.CouponPolicyRepository;
import com.nhnacademy.repository.UserCouponListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceImplTest {

    @Mock
    private CouponPolicyRepository couponPolicyRepository;
    @Mock
    private UserCouponListRepository userCouponListRepository;
    @Mock
    private CouponBookRepository couponBookRepository;
    @Mock
    private CouponCategoryRepository couponCategoryRepository;
    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private CouponServiceImpl couponService;

    private CouponPolicy testCouponPolicy;
    private UserCouponList testUserCoupon;

    @BeforeEach
    void setUp() {
        testCouponPolicy = CouponPolicy.builder()
                .couponId(1L)
                .couponName("Test Coupon")
                .couponDiscountType(CouponDiscountType.AMOUNT)
                .couponDiscountAmount(1000)
                .couponMinimumOrderAmount(10000)
                .couponMaximumDiscountAmount(5000)
                .couponScope(CouponScope.ALL)
                .couponExpiredAt(LocalDateTime.now().plusDays(30))
                .couponIssuePeriod(null)
                .couponType(CouponType.GENERAL)
                .couponCreatedAt(LocalDateTime.now())
                .build();

        testUserCoupon = UserCouponList.builder()
                .userCouponId(1L)
                .userNo(1L)
                .couponPolicy(testCouponPolicy)
                .issuedAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusDays(30))
                .status(UserCouponStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("쿠폰 정책 생성 - 성공 (전체 범위)")
    void createCouponPolicy_success_allScope() {
        CouponPolicyRequestDto request = CouponPolicyRequestDto.builder()
                .couponName("New Coupon")
                .couponDiscountType(CouponDiscountType.AMOUNT)
                .couponDiscountAmount(500)
                .couponMinimumOrderAmount(5000)
                .couponMaximumDiscountAmount(2000)
                .couponScope(CouponScope.ALL)
                .couponType(CouponType.GENERAL)
                .build();

        when(couponPolicyRepository.save(any(CouponPolicy.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CouponPolicy result = couponService.createCouponPolicy(request);

        assertThat(result).isNotNull();
        assertThat(result.getCouponName()).isEqualTo("New Coupon");
        verify(couponPolicyRepository, times(1)).save(any(CouponPolicy.class));
        verify(couponBookRepository, never()).save(any(CouponBook.class));
        verify(couponCategoryRepository, never()).save(any(CouponCategory.class));
    }

    @Test
    @DisplayName("쿠폰 정책 생성 - 성공 (도서 범위)")
    void createCouponPolicy_success_bookScope() {
        CouponPolicyRequestDto request = CouponPolicyRequestDto.builder()
                .couponName("Book Coupon")
                .couponDiscountType(CouponDiscountType.AMOUNT)
                .couponDiscountAmount(500)
                .couponMinimumOrderAmount(5000)
                .couponMaximumDiscountAmount(2000)
                .couponScope(CouponScope.BOOK)
                .bookIds(List.of(101L, 102L))
                .couponType(CouponType.GENERAL)
                .build();

        when(couponPolicyRepository.save(any(CouponPolicy.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(couponBookRepository.save(any(CouponBook.class))).thenReturn(mock(CouponBook.class));

        CouponPolicy result = couponService.createCouponPolicy(request);

        assertThat(result).isNotNull();
        assertThat(result.getCouponScope()).isEqualTo(CouponScope.BOOK);
        verify(couponPolicyRepository, times(1)).save(any(CouponPolicy.class));
        verify(couponBookRepository, times(2)).save(any(CouponBook.class));
        verify(couponCategoryRepository, never()).save(any(CouponCategory.class));
    }

    @Test
    @DisplayName("쿠폰 정책 생성 - 성공 (카테고리 범위)")
    void createCouponPolicy_success_categoryScope() {
        CouponPolicyRequestDto request = CouponPolicyRequestDto.builder()
                .couponName("Category Coupon")
                .couponDiscountType(CouponDiscountType.AMOUNT)
                .couponDiscountAmount(500)
                .couponMinimumOrderAmount(5000)
                .couponMaximumDiscountAmount(2000)
                .couponScope(CouponScope.CATEGORY)
                .categoryIds(List.of(201L, 202L))
                .couponType(CouponType.GENERAL)
                .build();

        when(couponPolicyRepository.save(any(CouponPolicy.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(couponCategoryRepository.save(any(CouponCategory.class))).thenReturn(mock(CouponCategory.class));

        CouponPolicy result = couponService.createCouponPolicy(request);

        assertThat(result).isNotNull();
        assertThat(result.getCouponScope()).isEqualTo(CouponScope.CATEGORY);
        verify(couponPolicyRepository, times(1)).save(any(CouponPolicy.class));
        verify(couponBookRepository, never()).save(any(CouponBook.class));
        verify(couponCategoryRepository, times(2)).save(any(CouponCategory.class));
    }

    @Test
    @DisplayName("모든 쿠폰 정책 조회 - 성공")
    void getAllCouponPolicies_success() {
        when(couponPolicyRepository.findAll()).thenReturn(List.of(testCouponPolicy));
        

        var result = couponService.getAllCouponPolicies();

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getCouponName()).isEqualTo("Test Coupon");
        verify(couponPolicyRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("ID로 쿠폰 정책 조회 - 성공")
    void getCouponPolicyById_success() {
        when(couponPolicyRepository.findById(anyLong())).thenReturn(Optional.of(testCouponPolicy));

        Optional<CouponPolicy> result = couponService.getCouponPolicyById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getCouponId()).isEqualTo(1L);
        verify(couponPolicyRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("ID로 쿠폰 정책 조회 - 실패 (찾을 수 없음)")
    void getCouponPolicyById_notFound() {
        when(couponPolicyRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<CouponPolicy> result = couponService.getCouponPolicyById(99L);

        assertThat(result).isNotPresent();
        verify(couponPolicyRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("사용자에게 쿠폰 발급 - 성공 (유효기간/만료일 없음, 기본 365일)")
    void issueCouponToUser_success_defaultExpiration() {
        testCouponPolicy.setCouponIssuePeriod(null);
        testCouponPolicy.setCouponExpiredAt(null);
        when(couponPolicyRepository.findById(anyLong())).thenReturn(Optional.of(testCouponPolicy));
        when(userCouponListRepository.save(any(UserCouponList.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserCouponList result = couponService.issueCouponToUser(1L, 1L);

        assertThat(result).isNotNull();
        assertThat(result.getExpiredAt()).isAfterOrEqualTo(LocalDateTime.now().plusDays(365).minusSeconds(1));
        verify(couponPolicyRepository, times(1)).findById(anyLong());
        verify(userCouponListRepository, times(1)).save(any(UserCouponList.class));
    }

    @Test
    @DisplayName("도서 쿠폰 발급 - 성공 (유효기간/만료일 없음, 기본 365일)")
    void issueBookCoupon_success_defaultExpiration() {
        IssueBookCouponRequestDto request = IssueBookCouponRequestDto.builder()
                .userId(1L)
                .couponPolicyId(1L)
                .bookId(101L)
                .build();

        testCouponPolicy.setCouponScope(CouponScope.BOOK);
        testCouponPolicy.setCouponIssuePeriod(null);
        testCouponPolicy.setCouponExpiredAt(null);
        when(couponPolicyRepository.findById(anyLong())).thenReturn(Optional.of(testCouponPolicy));
        when(couponBookRepository.existsByCouponPolicy_CouponIdAndBookId(anyLong(), anyLong())).thenReturn(true);
        when(userCouponListRepository.save(any(UserCouponList.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserCouponList result = couponService.issueBookCoupon(request);

        assertThat(result).isNotNull();
        assertThat(result.getExpiredAt()).isAfterOrEqualTo(LocalDateTime.now().plusDays(365).minusSeconds(1));
        verify(couponPolicyRepository, times(1)).findById(anyLong());
        verify(couponBookRepository, times(1)).existsByCouponPolicy_CouponIdAndBookId(anyLong(), anyLong());
        verify(userCouponListRepository, times(1)).save(any(UserCouponList.class));
    }

    @Test
    @DisplayName("활성 사용자 쿠폰 조회 - 성공")
    void getActiveUserCoupons_success() {
        when(userCouponListRepository.findActiveCouponsByUserNo(anyLong())).thenReturn(List.of(testUserCoupon));

        List<UserCouponList> result = couponService.getActiveUserCoupons(1L);

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getUserNo()).isEqualTo(1L);
        assertThat(result.get(0).getStatus()).isEqualTo(UserCouponStatus.ACTIVE);
        verify(userCouponListRepository, times(1)).findActiveCouponsByUserNo(anyLong());
    }

    @Test
    @DisplayName("사용된 사용자 쿠폰 조회 - 성공")
    void getUsedUserCoupons_success() {
        testUserCoupon.setStatus(UserCouponStatus.USED);
        when(userCouponListRepository.findUsedCouponsByUserNo(anyLong())).thenReturn(List.of(testUserCoupon));

        List<UserCouponList> result = couponService.getUsedUserCoupons(1L);

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getUserNo()).isEqualTo(1L);
        assertThat(result.get(0).getStatus()).isEqualTo(UserCouponStatus.USED);
        verify(userCouponListRepository, times(1)).findUsedCouponsByUserNo(anyLong());
    }

    @Test
    @DisplayName("쿠폰 정책 ID로 조회 - 성공")
    void getCouponPolicy_success() {
        when(couponPolicyRepository.findById(anyLong())).thenReturn(Optional.of(testCouponPolicy));

        CouponPolicy result = couponService.getCouponPolicy(1L);

        assertThat(result).isNotNull();
        assertThat(result.getCouponId()).isEqualTo(1L);
        verify(couponPolicyRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("쿠폰 정책 ID로 조회 - 실패 (찾을 수 없음)")
    void getCouponPolicy_notFound() {
        when(couponPolicyRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.getCouponPolicy(99L))
                .isInstanceOf(CouponNotFoundException.class)
                .hasMessageContaining("쿠폰 정책을 찾을 수 없습니다");
        verify(couponPolicyRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("웰컴 쿠폰 발급 - 성공")
    void issueWelcomeCoupon_success() {
        CouponPolicy welcomePolicy = CouponPolicy.builder()
                .couponId(2L)
                .couponName("Welcome Coupon")
                .couponType(CouponType.WELCOME)
                .couponDiscountType(CouponDiscountType.AMOUNT)
                .couponDiscountAmount(1000)
                .couponScope(CouponScope.ALL)
                .build();

        when(couponPolicyRepository.findByCouponType(CouponType.WELCOME)).thenReturn(Optional.of(welcomePolicy));
        when(couponPolicyRepository.findById(welcomePolicy.getCouponId())).thenReturn(Optional.of(welcomePolicy));
        when(userCouponListRepository.findByUserNoAndCouponPolicy(anyLong(), any(CouponPolicy.class))).thenReturn(Collections.emptyList());
        when(userCouponListRepository.save(any(UserCouponList.class))).thenReturn(testUserCoupon); // issueCouponToUser 내부 호출

        UserCouponList result = couponService.issueWelcomeCoupon(1L);

        assertThat(result).isNotNull();
        assertThat(result.getUserNo()).isEqualTo(1L);
        verify(couponPolicyRepository, times(1)).findByCouponType(CouponType.WELCOME);
        verify(userCouponListRepository, times(1)).findByUserNoAndCouponPolicy(anyLong(), any(CouponPolicy.class));
        verify(userCouponListRepository, times(1)).save(any(UserCouponList.class));
    }

    @Test
    @DisplayName("웰컴 쿠폰 발급 - 실패 (정책 없음)")
    void issueWelcomeCoupon_policyNotFound() {
        when(couponPolicyRepository.findByCouponType(CouponType.WELCOME)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.issueWelcomeCoupon(1L))
                .isInstanceOf(WelcomeCouponPolicyNotFoundException.class)
                .hasMessageContaining("Welcome 쿠폰 정책을 찾을 수 없습니다.");
        verify(couponPolicyRepository, times(1)).findByCouponType(CouponType.WELCOME);
        verify(userCouponListRepository, never()).findByUserNoAndCouponPolicy(anyLong(), any(CouponPolicy.class));
    }

    @Test
    @DisplayName("웰컴 쿠폰 발급 - 실패 (이미 발급됨)")
    void issueWelcomeCoupon_alreadyIssued() {
        CouponPolicy welcomePolicy = CouponPolicy.builder()
                .couponId(2L)
                .couponName("Welcome Coupon")
                .couponType(CouponType.WELCOME)
                .couponDiscountType(CouponDiscountType.AMOUNT)
                .couponDiscountAmount(1000)
                .couponScope(CouponScope.ALL)
                .build();

        when(couponPolicyRepository.findByCouponType(CouponType.WELCOME)).thenReturn(Optional.of(welcomePolicy));
        when(userCouponListRepository.findByUserNoAndCouponPolicy(anyLong(), any(CouponPolicy.class))).thenReturn(List.of(testUserCoupon));

        assertThatThrownBy(() -> couponService.issueWelcomeCoupon(1L))
                .isInstanceOf(CouponAlreadyExistException.class)
                .hasMessageContaining("웰컴 쿠폰이 이미 발급되었습니다.");
        verify(couponPolicyRepository, times(1)).findByCouponType(CouponType.WELCOME);
        verify(userCouponListRepository, times(1)).findByUserNoAndCouponPolicy(anyLong(), any(CouponPolicy.class));
        verify(userCouponListRepository, never()).save(any(UserCouponList.class));
    }

    @Test
    @DisplayName("생일 쿠폰 발급 - 성공")
    void issueBirthdayCoupon_success() {
        CouponPolicy birthdayPolicy = CouponPolicy.builder()
                .couponId(3L)
                .couponName("Birthday Coupon")
                .couponType(CouponType.BIRTHDAY)
                .couponDiscountType(CouponDiscountType.AMOUNT)
                .couponDiscountAmount(1000)
                .couponScope(CouponScope.ALL)
                .build();

        when(couponPolicyRepository.findByCouponType(CouponType.BIRTHDAY)).thenReturn(Optional.of(birthdayPolicy));
        when(userCouponListRepository.findByUserNoAndCouponPolicy(anyLong(), any(CouponPolicy.class))).thenReturn(Collections.emptyList());
        when(userCouponListRepository.save(any(UserCouponList.class))).thenReturn(testUserCoupon);

        UserCouponList result = couponService.issueBirthdayCoupon(1L, LocalDate.of(2000, 7, 23));

        assertThat(result).isNotNull();
        assertThat(result.getUserNo()).isEqualTo(1L);
        verify(couponPolicyRepository, times(1)).findByCouponType(CouponType.BIRTHDAY);
        verify(userCouponListRepository, times(1)).findByUserNoAndCouponPolicy(anyLong(), any(CouponPolicy.class));
        verify(userCouponListRepository, times(1)).save(any(UserCouponList.class));
    }

    @Test
    @DisplayName("생일 쿠폰 발급 - 실패 (정책 없음)")
    void issueBirthdayCoupon_policyNotFound() {
        when(couponPolicyRepository.findByCouponType(CouponType.BIRTHDAY)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.issueBirthdayCoupon(1L, LocalDate.of(2000, 7, 23)))
                .isInstanceOf(CouponNotFoundException.class)
                .hasMessageContaining("Birthday 쿠폰 정책을 찾을 수 없습니다.");
        verify(couponPolicyRepository, times(1)).findByCouponType(CouponType.BIRTHDAY);
        verify(userCouponListRepository, never()).findByUserNoAndCouponPolicy(anyLong(), any(CouponPolicy.class));
    }

    @Test
    @DisplayName("생일 쿠폰 발급 - 실패 (이미 올해 발급됨)")
    void issueBirthdayCoupon_alreadyIssuedThisYear() {
        CouponPolicy birthdayPolicy = CouponPolicy.builder()
                .couponId(3L)
                .couponName("Birthday Coupon")
                .couponType(CouponType.BIRTHDAY)
                .couponDiscountType(CouponDiscountType.AMOUNT)
                .couponDiscountAmount(1000)
                .couponScope(CouponScope.ALL)
                .build();
        UserCouponList issuedThisYear = UserCouponList.builder()
                .userNo(1L)
                .couponPolicy(birthdayPolicy)
                .issuedAt(LocalDateTime.now())
                .build();

        when(couponPolicyRepository.findByCouponType(CouponType.BIRTHDAY)).thenReturn(Optional.of(birthdayPolicy));
        when(userCouponListRepository.findByUserNoAndCouponPolicy(anyLong(), any(CouponPolicy.class))).thenReturn(List.of(issuedThisYear));

        assertThatThrownBy(() -> couponService.issueBirthdayCoupon(1L, LocalDate.of(2000, 7, 23)))
                .isInstanceOf(CouponAlreadyExistException.class)
                .hasMessageContaining("이번 연도 생일 쿠폰이 이미 발급되었습니다.");
        verify(couponPolicyRepository, times(1)).findByCouponType(CouponType.BIRTHDAY);
        verify(userCouponListRepository, times(1)).findByUserNoAndCouponPolicy(anyLong(), any(CouponPolicy.class));
        verify(userCouponListRepository, never()).save(any(UserCouponList.class));
    }

    @Test
    @DisplayName("쿠폰 사용 - 성공")
    void useCoupon_success() {
        when(userCouponListRepository.findByUserNoAndUserCouponId(anyLong(), anyLong())).thenReturn(Optional.of(testUserCoupon));
        when(userCouponListRepository.save(any(UserCouponList.class))).thenReturn(testUserCoupon);

        couponService.useCoupon(1L, 1L, 100L);

        assertThat(testUserCoupon.getStatus()).isEqualTo(UserCouponStatus.USED);
        assertThat(testUserCoupon.getOrderId()).isEqualTo(100L);
        verify(userCouponListRepository, times(1)).findByUserNoAndUserCouponId(anyLong(), anyLong());
        verify(userCouponListRepository, times(1)).save(any(UserCouponList.class));
    }

    @Test
    @DisplayName("쿠폰 사용 - 실패 (쿠폰 찾을 수 없음)")
    void useCoupon_notFound() {
        when(userCouponListRepository.findByUserNoAndUserCouponId(anyLong(), anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.useCoupon(1L, 99L, 100L))
                .isInstanceOf(UserCouponNotFoundException.class)
                .hasMessageContaining("쿠폰을 찾을 수 없습니다.");
        verify(userCouponListRepository, times(1)).findByUserNoAndUserCouponId(anyLong(), anyLong());
        verify(userCouponListRepository, never()).save(any(UserCouponList.class));
    }

    @Test
    @DisplayName("쿠폰 사용 - 실패 (이미 사용됨)")
    void useCoupon_alreadyUsed() {
        testUserCoupon.setStatus(UserCouponStatus.USED);
        when(userCouponListRepository.findByUserNoAndUserCouponId(anyLong(), anyLong())).thenReturn(Optional.of(testUserCoupon));

        assertThatThrownBy(() -> couponService.useCoupon(1L, 1L, 100L))
                .isInstanceOf(CouponAlreadyUsedException.class)
                .hasMessageContaining("이미 사용된 쿠폰입니다.");
        verify(userCouponListRepository, times(1)).findByUserNoAndUserCouponId(anyLong(), anyLong());
        verify(userCouponListRepository, never()).save(any(UserCouponList.class));
    }

    @Test
    @DisplayName("쿠폰 사용 - 실패 (만료됨)")
    void useCoupon_expired() {
        testUserCoupon.setExpiredAt(LocalDateTime.now().minusDays(1));
        when(userCouponListRepository.findByUserNoAndUserCouponId(anyLong(), anyLong())).thenReturn(Optional.of(testUserCoupon));

        assertThatThrownBy(() -> couponService.useCoupon(1L, 1L, 100L))
                .isInstanceOf(CouponExpiredException.class)
                .hasMessageContaining("만료된 쿠폰입니다.");
        verify(userCouponListRepository, times(1)).findByUserNoAndUserCouponId(anyLong(), anyLong());
        verify(userCouponListRepository, never()).save(any(UserCouponList.class));
    }

    @Test
    @DisplayName("쿠폰 정책 삭제 - 성공")
    void deleteCouponPolicy_success() {
        when(couponPolicyRepository.findById(anyLong())).thenReturn(Optional.of(testCouponPolicy));
        doNothing().when(couponBookRepository).deleteByCouponPolicy(any(CouponPolicy.class));
        doNothing().when(couponCategoryRepository).deleteByCouponPolicy(any(CouponPolicy.class));
        doNothing().when(userCouponListRepository).deleteByCouponPolicy(any(CouponPolicy.class));
        doNothing().when(couponPolicyRepository).delete(any(CouponPolicy.class));

        couponService.deleteCouponPolicy(1L);

        verify(couponPolicyRepository, times(1)).findById(anyLong());
        verify(couponBookRepository, times(1)).deleteByCouponPolicy(any(CouponPolicy.class));
        verify(couponCategoryRepository, times(1)).deleteByCouponPolicy(any(CouponPolicy.class));
        verify(userCouponListRepository, times(1)).deleteByCouponPolicy(any(CouponPolicy.class));
        verify(couponPolicyRepository, times(1)).delete(any(CouponPolicy.class));
    }

    @Test
    @DisplayName("쿠폰 정책 삭제 - 실패 (정책 없음)")
    void deleteCouponPolicy_notFound() {
        when(couponPolicyRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.deleteCouponPolicy(99L))
                .isInstanceOf(CouponNotFoundException.class);
        verify(couponPolicyRepository, times(1)).findById(anyLong());
        verify(couponBookRepository, never()).deleteByCouponPolicy(any(CouponPolicy.class));
        verify(couponCategoryRepository, never()).deleteByCouponPolicy(any(CouponPolicy.class));
        verify(userCouponListRepository, never()).deleteByCouponPolicy(any(CouponPolicy.class));
        verify(couponPolicyRepository, never()).delete(any(CouponPolicy.class));
    }

    @Test
    @DisplayName("할인 금액 계산 - 성공 (정액 할인)")
    void calculateDiscountAmount_amountType_success() {
        testUserCoupon.getCouponPolicy().setCouponDiscountType(CouponDiscountType.AMOUNT);
        testUserCoupon.getCouponPolicy().setCouponDiscountAmount(1000);
        testUserCoupon.getCouponPolicy().setCouponMinimumOrderAmount(5000);
        testUserCoupon.getCouponPolicy().setCouponScope(CouponScope.ALL);

        when(userCouponListRepository.findByUserNoAndUserCouponId(anyLong(), anyLong())).thenReturn(Optional.of(testUserCoupon));

        int discount = couponService.calculateDiscountAmount(1L, 1L, 10000, Collections.emptyList(), Collections.emptyList());

        assertThat(discount).isEqualTo(1000);
        verify(userCouponListRepository, times(1)).findByUserNoAndUserCouponId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("할인 금액 계산 - 성공 (정률 할인, 최대 할인 금액 미적용)")
    void calculateDiscountAmount_percentType_noMaxDiscount() {
        testUserCoupon.getCouponPolicy().setCouponDiscountType(CouponDiscountType.PERCENT);
        testUserCoupon.getCouponPolicy().setCouponDiscountAmount(10); // 10%
        testUserCoupon.getCouponPolicy().setCouponMinimumOrderAmount(5000);
        testUserCoupon.getCouponPolicy().setCouponMaximumDiscountAmount(null); // No max discount
        testUserCoupon.getCouponPolicy().setCouponScope(CouponScope.ALL);

        when(userCouponListRepository.findByUserNoAndUserCouponId(anyLong(), anyLong())).thenReturn(Optional.of(testUserCoupon));

        int discount = couponService.calculateDiscountAmount(1L, 1L, 10000, Collections.emptyList(), Collections.emptyList());

        assertThat(discount).isEqualTo(1000); // 10000 * 0.10
        verify(userCouponListRepository, times(1)).findByUserNoAndUserCouponId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("할인 금액 계산 - 성공 (정률 할인, 최대 할인 금액 적용)")
    void calculateDiscountAmount_percentType_withMaxDiscount() {
        testUserCoupon.getCouponPolicy().setCouponDiscountType(CouponDiscountType.PERCENT);
        testUserCoupon.getCouponPolicy().setCouponDiscountAmount(20); // 20%
        testUserCoupon.getCouponPolicy().setCouponMinimumOrderAmount(5000);
        testUserCoupon.getCouponPolicy().setCouponMaximumDiscountAmount(1500); // Max 1500
        testUserCoupon.getCouponPolicy().setCouponScope(CouponScope.ALL);

        when(userCouponListRepository.findByUserNoAndUserCouponId(anyLong(), anyLong())).thenReturn(Optional.of(testUserCoupon));

        int discount = couponService.calculateDiscountAmount(1L, 1L, 10000, Collections.emptyList(), Collections.emptyList());

        assertThat(discount).isEqualTo(1500); // 10000 * 0.20 = 2000, but max is 1500
        verify(userCouponListRepository, times(1)).findByUserNoAndUserCouponId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("할인 금액 계산 - 실패 (쿠폰 찾을 수 없음)")
    void calculateDiscountAmount_userCouponNotFound() {
        when(userCouponListRepository.findByUserNoAndUserCouponId(anyLong(), anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.calculateDiscountAmount(1L, 99L, 10000, Collections.emptyList(), Collections.emptyList()))
                .isInstanceOf(UserCouponNotFoundException.class);
        verify(userCouponListRepository, times(1)).findByUserNoAndUserCouponId(anyLong(), anyLong());
    }



    @Test
    @DisplayName("할인 금액 계산 - 실패 (쿠폰 만료)")
    void calculateDiscountAmount_expiredCoupon() {
        testUserCoupon.setExpiredAt(LocalDateTime.now().minusDays(1));
        when(userCouponListRepository.findByUserNoAndUserCouponId(anyLong(), anyLong())).thenReturn(Optional.of(testUserCoupon));

        assertThatThrownBy(() -> couponService.calculateDiscountAmount(1L, 1L, 10000, Collections.emptyList(), Collections.emptyList()))
                .isInstanceOf(CouponExpiredException.class)
                .hasMessageContaining("만료된 쿠폰입니다.");
        verify(userCouponListRepository, times(1)).findByUserNoAndUserCouponId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("할인 금액 계산 - 실패 (최소 주문 금액 미달)")
    void calculateDiscountAmount_minimumOrderAmountNotMet() {
        testUserCoupon.getCouponPolicy().setCouponMinimumOrderAmount(20000); // Min 20000
        when(userCouponListRepository.findByUserNoAndUserCouponId(anyLong(), anyLong())).thenReturn(Optional.of(testUserCoupon));

        assertThatThrownBy(() -> couponService.calculateDiscountAmount(1L, 1L, 10000, Collections.emptyList(), Collections.emptyList()))
                .isInstanceOf(CouponNotApplicableException.class)
                .hasMessageContaining("최소 주문 금액");
        verify(userCouponListRepository, times(1)).findByUserNoAndUserCouponId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("할인 금액 계산 - 실패 (도서 범위 쿠폰, 주문에 해당 도서 없음)")
    void calculateDiscountAmount_bookScope_bookNotIncluded() {
        testUserCoupon.getCouponPolicy().setCouponScope(CouponScope.BOOK);
        when(userCouponListRepository.findByUserNoAndUserCouponId(anyLong(), anyLong())).thenReturn(Optional.of(testUserCoupon));
        when(couponBookRepository.existsByCouponPolicyIdAndBookIdsIn(anyLong(), anyList())).thenReturn(false);

        assertThatThrownBy(() -> couponService.calculateDiscountAmount(1L, 1L, 10000, List.of(999L), Collections.emptyList()))
                .isInstanceOf(CouponNotApplicableException.class)
                .hasMessageContaining("주문에 쿠폰 적용 대상 도서가 포함되어 있지 않습니다.");
        verify(userCouponListRepository, times(1)).findByUserNoAndUserCouponId(anyLong(), anyLong());
        verify(couponBookRepository, times(1)).existsByCouponPolicyIdAndBookIdsIn(anyLong(), anyList());
    }

    @Test
    @DisplayName("할인 금액 계산 - 실패 (카테고리 범위 쿠폰, 주문에 해당 카테고리 없음)")
    void calculateDiscountAmount_categoryScope_categoryNotIncluded() {
        testUserCoupon.getCouponPolicy().setCouponScope(CouponScope.CATEGORY);
        when(userCouponListRepository.findByUserNoAndUserCouponId(anyLong(), anyLong())).thenReturn(Optional.of(testUserCoupon));
        when(couponCategoryRepository.existsByCouponPolicyIdAndCategoryIdsIn(anyLong(), anyList())).thenReturn(false);

        assertThatThrownBy(() -> couponService.calculateDiscountAmount(1L, 1L, 10000, Collections.emptyList(), List.of(999L)))
                .isInstanceOf(CouponNotApplicableException.class)
                .hasMessageContaining("주문에 쿠폰 적용 대상 카테고리가 포함되어 있지 않습니다.");
        verify(userCouponListRepository, times(1)).findByUserNoAndUserCouponId(anyLong(), anyLong());
        verify(couponCategoryRepository, times(1)).existsByCouponPolicyIdAndCategoryIdsIn(anyLong(), anyList());
    }

    @Test
    @DisplayName("쿠폰 발행 프로세스 시작 - 성공")
    void startCouponIssuingProcess_success() {
        when(couponPolicyRepository.findById(anyLong())).thenReturn(Optional.of(testCouponPolicy));
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), eq(1L));

        couponService.startCouponIssuingProcess(1L);

        verify(couponPolicyRepository, times(1)).findById(anyLong());
        verify(rabbitTemplate, times(1)).convertAndSend(
                eq(RabbitMQConfig.COUPON_ISSUING_STARTED_EXCHANGE),
                eq(RabbitMQConfig.COUPON_ISSUING_STARTED_ROUTING_KEY),
                eq(1L)
        );
    }

//    @Test
//    @DisplayName("쿠폰 발행 프로세스 시작 - 실패 (정책 없음)")
//    void startCouponIssuingProcess_policyNotFound() {
//        when(couponPolicyRepository.findById(anyLong())).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> couponService.startCouponIssuingProcess(99L))
//                .isInstanceOf(CouponNotFoundException.class);
//        verify(couponPolicyRepository, times(1)).findById(anyLong());
//        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any());
//    }

    @Test
    @DisplayName("도서에 쿠폰 발행 - 성공")
    void issueCouponToBook_success() {
        testCouponPolicy.setCouponScope(CouponScope.BOOK);
        when(couponPolicyRepository.findById(anyLong())).thenReturn(Optional.of(testCouponPolicy));
        when(couponBookRepository.save(any(CouponBook.class))).thenReturn(mock(CouponBook.class));

        couponService.issueCouponToBook(1L, 101L);

        verify(couponPolicyRepository, times(1)).findById(anyLong());
        verify(couponBookRepository, times(1)).save(any(CouponBook.class));
    }

    @Test
    @DisplayName("도서에 쿠폰 발행 - 실패 (정책 없음)")
    void issueCouponToBook_policyNotFound() {
        when(couponPolicyRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.issueCouponToBook(99L, 101L))
                .isInstanceOf(CouponNotFoundException.class);
        verify(couponPolicyRepository, times(1)).findById(anyLong());
        verify(couponBookRepository, never()).save(any(CouponBook.class));
    }

    @Test
    @DisplayName("도서에 쿠폰 발행 - 실패 (도서 전용 쿠폰 아님)")
    void issueCouponToBook_notBookScope() {
        testCouponPolicy.setCouponScope(CouponScope.ALL); // Not BOOK scope
        when(couponPolicyRepository.findById(anyLong())).thenReturn(Optional.of(testCouponPolicy));

        assertThatThrownBy(() -> couponService.issueCouponToBook(1L, 101L))
                .isInstanceOf(CouponNotApplicableException.class)
                .hasMessageContaining("해당 쿠폰 정책은 도서 전용 쿠폰이 아닙니다.");
        verify(couponPolicyRepository, times(1)).findById(anyLong());
        verify(couponBookRepository, never()).save(any(CouponBook.class));
    }

    @Test
    @DisplayName("카테고리 쿠폰 발급 - 성공")
    void issueCategoryCoupon_success() {
        testCouponPolicy.setCouponScope(CouponScope.CATEGORY);
        when(couponPolicyRepository.findById(anyLong())).thenReturn(Optional.of(testCouponPolicy));
        when(couponCategoryRepository.existsByCouponPolicy_CouponIdAndCategoryId(anyLong(), anyLong())).thenReturn(true);
        when(userCouponListRepository.save(any(UserCouponList.class))).thenReturn(testUserCoupon);

        UserCouponList result = couponService.issueCategoryCoupon(1L, 1L, 201L);

        assertThat(result).isNotNull();
        assertThat(result.getUserNo()).isEqualTo(1L);
        verify(couponPolicyRepository, times(1)).findById(anyLong());
        verify(couponCategoryRepository, times(1)).existsByCouponPolicy_CouponIdAndCategoryId(anyLong(), anyLong());
        verify(userCouponListRepository, times(1)).save(any(UserCouponList.class));
    }

    @Test
    @DisplayName("카테고리 쿠폰 발급 - 실패 (정책 없음)")
    void issueCategoryCoupon_policyNotFound() {
        when(couponPolicyRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.issueCategoryCoupon(1L, 99L, 201L))
                .isInstanceOf(CouponNotFoundException.class);
        verify(couponPolicyRepository, times(1)).findById(anyLong());
        verify(couponCategoryRepository, never()).existsByCouponPolicy_CouponIdAndCategoryId(anyLong(), anyLong());
        verify(userCouponListRepository, never()).save(any(UserCouponList.class));
    }

    @Test
    @DisplayName("카테고리 쿠폰 발급 - 실패 (카테고리 전용 쿠폰 아님)")
    void issueCategoryCoupon_notCategoryScope() {
        testCouponPolicy.setCouponScope(CouponScope.ALL); // Not CATEGORY scope
        when(couponPolicyRepository.findById(anyLong())).thenReturn(Optional.of(testCouponPolicy));

        assertThatThrownBy(() -> couponService.issueCategoryCoupon(1L, 1L, 201L))
                .isInstanceOf(CouponNotApplicableException.class)
                .hasMessageContaining("해당 쿠폰 정책은 카테고리 전용 쿠폰이 아닙니다.");
        verify(couponPolicyRepository, times(1)).findById(anyLong());
        verify(couponCategoryRepository, never()).existsByCouponPolicy_CouponIdAndCategoryId(anyLong(), anyLong());
        verify(userCouponListRepository, never()).save(any(UserCouponList.class));
    }

    @Test
    @DisplayName("카테고리 쿠폰 발급 - 실패 (해당 카테고리에 적용되는 쿠폰 아님)")
    void issueCategoryCoupon_categoryNotApplicable() {
        testCouponPolicy.setCouponScope(CouponScope.CATEGORY);
        when(couponPolicyRepository.findById(anyLong())).thenReturn(Optional.of(testCouponPolicy));
        when(couponCategoryRepository.existsByCouponPolicy_CouponIdAndCategoryId(anyLong(), anyLong())).thenReturn(false);

        assertThatThrownBy(() -> couponService.issueCategoryCoupon(1L, 1L, 201L))
                .isInstanceOf(CouponNotApplicableException.class)
                .hasMessageContaining("해당 카테고리에 적용되는 쿠폰 정책이 아닙니다.");
        verify(couponPolicyRepository, times(1)).findById(anyLong());
        verify(couponCategoryRepository, times(1)).existsByCouponPolicy_CouponIdAndCategoryId(anyLong(), anyLong());
        verify(userCouponListRepository, never()).save(any(UserCouponList.class));
    }

    @Test
    @DisplayName("쿠폰 ID로 도서 ID 목록 조회 - 성공")
    void getBookIdsByCouponId_success() {
        List<Long> bookIds = List.of(1L, 2L, 3L);
        when(couponBookRepository.findBookIdsByCouponId(anyLong())).thenReturn(bookIds);

        List<Long> result = couponService.getBookIdsByCouponId(1L);

        assertThat(result).isEqualTo(bookIds);
        verify(couponBookRepository, times(1)).findBookIdsByCouponId(anyLong());
    }

    @Test
    @DisplayName("쿠폰 ID로 카테고리 ID 목록 조회 - 성공")
    void getCategoryIdsByCouponId_success() {
        List<Long> categoryIds = List.of(10L, 20L, 30L);
        when(couponCategoryRepository.findCategoryIdsByCouponId(anyLong())).thenReturn(categoryIds);

        List<Long> result = couponService.getCategoryIdsByCouponId(1L);

        assertThat(result).isEqualTo(categoryIds);
        verify(couponCategoryRepository, times(1)).findCategoryIdsByCouponId(anyLong());
    }
}
