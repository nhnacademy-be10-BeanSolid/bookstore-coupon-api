package com.nhnacademy.service;

import com.nhnacademy.domain.*;
import com.nhnacademy.domain.enumtype.CouponDiscountType;
import com.nhnacademy.domain.enumtype.CouponScope;
import com.nhnacademy.domain.enumtype.CouponType;
import com.nhnacademy.domain.enumtype.UserCouponStatus;
import com.nhnacademy.dto.request.CouponPolicyRequestDto;
import com.nhnacademy.dto.request.IssueBookCouponRequestDto;
import com.nhnacademy.exception.*;
import com.nhnacademy.repository.*;
import com.nhnacademy.service.impl.CouponServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

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

    private CouponPolicyRequestDto couponPolicyRequestDto;
    private CouponPolicy couponPolicy;

    @BeforeEach
    void setUp() {
        couponPolicyRequestDto = CouponPolicyRequestDto.builder()
                .couponName("Test Coupon")
                .couponDiscountType(CouponDiscountType.PERCENT)
                .couponDiscountAmount(10)
                .couponMinimumOrderAmount(10000)
                .couponMaximumDiscountAmount(2000)
                .couponScope(CouponScope.ALL)
                .couponType(CouponType.GENERAL)
                .couponIssuePeriod(30)
                .couponExpiredAt(LocalDateTime.now().plusDays(30))
                .build();

        couponPolicy = CouponPolicy.builder()
                .couponId(1L)
                .couponName("Test Coupon")
                .couponDiscountType(CouponDiscountType.PERCENT)
                .couponDiscountAmount(10)
                .couponMinimumOrderAmount(10000)
                .couponMaximumDiscountAmount(2000)
                .couponScope(CouponScope.ALL)
                .couponType(CouponType.GENERAL)
                .couponIssuePeriod(30)
                .couponExpiredAt(LocalDateTime.now().plusDays(30))
                .build();
    }

    @Test
    @DisplayName("쿠폰 정책 생성 - BOOK 스코프일 때 BOOK 연관 저장 거르는 로직 함께 테스트")
    void testCreateCouponPolicy_BookScope() {
        couponPolicyRequestDto = CouponPolicyRequestDto.builder()
                .couponName("Book Coupon")
                .couponDiscountType(CouponDiscountType.PERCENT)
                .couponDiscountAmount(15)
                .couponMinimumOrderAmount(5000)
                .couponMaximumDiscountAmount(1000)
                .couponScope(CouponScope.BOOK)
                .couponType(CouponType.GENERAL)
                .couponIssuePeriod(20)
                .bookIds(List.of(101L, 102L))
                .build();

        when(couponPolicyRepository.save(any(CouponPolicy.class))).thenReturn(couponPolicy);

        couponService.createCouponPolicy(couponPolicyRequestDto);

        verify(couponPolicyRepository).save(any(CouponPolicy.class));
        verify(couponBookRepository, times(2)).save(any());
        verify(couponCategoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("쿠폰 정책 생성 - CATEGORY 스코프일 때 CATEGORY 연관 저장 거르는 로직 함께 테스트")
    void testCreateCouponPolicy_CategoryScope() {
        couponPolicyRequestDto = CouponPolicyRequestDto.builder()
                .couponName("Category Coupon")
                .couponDiscountType(CouponDiscountType.AMOUNT)
                .couponDiscountAmount(2000)
                .couponMinimumOrderAmount(10000)
                .couponMaximumDiscountAmount(3000)
                .couponScope(CouponScope.CATEGORY)
                .couponType(CouponType.GENERAL)
                .couponIssuePeriod(15)
                .categoryIds(List.of(201L, 202L))
                .build();

        when(couponPolicyRepository.save(any(CouponPolicy.class))).thenReturn(couponPolicy);

        couponService.createCouponPolicy(couponPolicyRequestDto);

        verify(couponPolicyRepository).save(any(CouponPolicy.class));
        verify(couponCategoryRepository, times(2)).save(any());
        verify(couponBookRepository, never()).save(any());
    }

    @Test
    @DisplayName("쿠폰 정책 생성 - ALL 스코프일 때 BOOK, CATEGORY 저장 안됨")
    void testCreateCouponPolicy_AllScope() {
        when(couponPolicyRepository.save(any(CouponPolicy.class))).thenReturn(couponPolicy);

        couponService.createCouponPolicy(couponPolicyRequestDto);

        verify(couponPolicyRepository).save(any(CouponPolicy.class));
        verifyNoInteractions(couponBookRepository);
        verifyNoInteractions(couponCategoryRepository);
    }

    @Test
    @DisplayName("특정 쿠폰 정책 조회 - 존재할 때 반환")
    void testGetCouponPolicyById_Exists() {
        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.of(couponPolicy));

        Optional<CouponPolicy> result = couponService.getCouponPolicyById(1L);

        assertTrue(result.isPresent());
        assertEquals("Test Coupon", result.get().getCouponName());
    }

    @Test
    @DisplayName("특정 쿠폰 정책 조회 - 없을 때 Optional.empty 반환")
    void testGetCouponPolicyById_NotExists() {
        when(couponPolicyRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<CouponPolicy> result = couponService.getCouponPolicyById(2L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("사용자 쿠폰 발급 정상 케이스 - 유효기간 계산 포함")
    void testIssueCouponToUser_Success() {
        Long userNo = 100L;
        couponPolicy = CouponPolicy.builder()
                .couponId(1L)
                .couponName("Issue Test Coupon")
                .couponDiscountType(CouponDiscountType.PERCENT)
                .couponDiscountAmount(20)
                .couponMinimumOrderAmount(5000)
                .couponMaximumDiscountAmount(2000)
                .couponScope(CouponScope.ALL)
                .couponType(CouponType.GENERAL)
                .couponIssuePeriod(10)
                .couponExpiredAt(LocalDateTime.now().plusDays(20))
                .build();

        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.of(couponPolicy));
        when(userCouponListRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        UserCouponList userCouponList = couponService.issueCouponToUser(userNo, 1L);

        assertNotNull(userCouponList);
        assertEquals(userNo, userCouponList.getUserNo());
        assertEquals(couponPolicy, userCouponList.getCouponPolicy());
        assertEquals(UserCouponStatus.ACTIVE, userCouponList.getStatus());
        assertTrue(userCouponList.getExpiredAt().isAfter(LocalDateTime.now()));
    }

    @Test
    @DisplayName("사용자 쿠폰 발급 중 만료된 쿠폰 정책 예외 발생")
    void testIssueCouponToUser_CouponExpiredException() {

        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.of(couponPolicy));

        CouponExpiredException exception = assertThrows(CouponExpiredException.class,
                () -> couponService.issueCouponToUser(100L, 1L));
        assertTrue(exception.getMessage().contains("만료된 쿠폰 정책입니다"));
    }

    @Test
    @DisplayName("도서 전용 쿠폰이 아닌 정책으로 도서 쿠폰 발급시 예외")
    void testIssueBookCoupon_NotBookScope() {

        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.of(couponPolicy));

        IssueBookCouponRequestDto requestDto = IssueBookCouponRequestDto.builder()
                .userId(100L)
                .couponPolicyId(1L)
                .bookId(100L)
                .build();

        CouponNotApplicableException ex = assertThrows(CouponNotApplicableException.class,
                () -> couponService.issueBookCoupon(requestDto));
        assertTrue(ex.getMessage().contains("도서 전용 쿠폰이 아닙니다"));
    }

    @Test
    @DisplayName("도서 쿠폰 발급 - 적용 도서 아닐 때 예외")
    void testIssueBookCoupon_NotApplicableBook() {


        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.of(couponPolicy));
        when(couponBookRepository.existsByCouponPolicy_CouponIdAndBookId(1L, 100L)).thenReturn(false);

        IssueBookCouponRequestDto requestDto = IssueBookCouponRequestDto.builder()
                .userId(100L)
                .couponPolicyId(1L)
                .bookId(100L)
                .build();

        CouponNotApplicableException ex = assertThrows(CouponNotApplicableException.class,
                () -> couponService.issueBookCoupon(requestDto));
        assertTrue(ex.getMessage().contains("적용되는 쿠폰 정책이 아닙니다"));
    }
}
