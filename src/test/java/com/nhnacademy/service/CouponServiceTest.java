package com.nhnacademy.service;

import com.nhnacademy.common.exception.CouponAlreadyExistException;
import com.nhnacademy.domain.*;
import com.nhnacademy.domain.enumtype.CouponDiscountType;
import com.nhnacademy.domain.enumtype.CouponScope;
import com.nhnacademy.domain.enumtype.CouponType;
import com.nhnacademy.domain.enumtype.UserCouponStatus;
import com.nhnacademy.dto.request.CouponPolicyRequestDto;
import com.nhnacademy.dto.request.IssueBookCouponRequestDto;
import com.nhnacademy.dto.response.CouponPolicyResponseDto;
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

import java.time.LocalDate;
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
    private CouponPolicy welcomeCouponPolicy;
    private CouponPolicy birthCouponPolicy;
    private CouponPolicy bookCouponPolicy;
    private CouponPolicy categoryCouponPolicy;

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

        welcomeCouponPolicy = CouponPolicy.builder()
                .couponId(2L)
                .couponName("Welcome Coupon")
                .couponDiscountType(CouponDiscountType.PERCENT)
                .couponDiscountAmount(10)
                .couponMinimumOrderAmount(10000)
                .couponMaximumDiscountAmount(2000)
                .couponScope(CouponScope.ALL)
                .couponType(CouponType.WELCOME)
                .couponIssuePeriod(30)
                .couponExpiredAt(LocalDateTime.now().plusDays(30))
                .build();

        birthCouponPolicy = CouponPolicy.builder()
                .couponId(3L)
                .couponName("Birth Coupon")
                .couponDiscountType(CouponDiscountType.PERCENT)
                .couponDiscountAmount(10)
                .couponMinimumOrderAmount(10000)
                .couponMaximumDiscountAmount(2000)
                .couponScope(CouponScope.ALL)
                .couponType(CouponType.BIRTHDAY)
                .couponIssuePeriod(30)
                .couponExpiredAt(LocalDateTime.now().plusDays(30))
                .build();

        bookCouponPolicy = CouponPolicy.builder()
                .couponId(4L)
                .couponName("Book Coupon")
                .couponDiscountType(CouponDiscountType.PERCENT)
                .couponDiscountAmount(10)
                .couponMinimumOrderAmount(10000)
                .couponMaximumDiscountAmount(2000)
                .couponScope(CouponScope.BOOK)
                .couponType(CouponType.GENERAL)
                .couponIssuePeriod(30)
                .couponExpiredAt(LocalDateTime.now().plusDays(30))
                .build();

        categoryCouponPolicy = CouponPolicy.builder()
                .couponId(5L)
                .couponName("Category Coupon")
                .couponDiscountType(CouponDiscountType.PERCENT)
                .couponDiscountAmount(10)
                .couponMinimumOrderAmount(10000)
                .couponMaximumDiscountAmount(2000)
                .couponScope(CouponScope.CATEGORY)
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

        CouponPolicy testCouponPolicy = CouponPolicy.builder()
                .couponId(2L)
                .couponName("Test Coupon")
                .couponDiscountType(CouponDiscountType.PERCENT)
                .couponDiscountAmount(10)
                .couponMinimumOrderAmount(10000)
                .couponMaximumDiscountAmount(2000)
                .couponScope(CouponScope.ALL)
                .couponType(CouponType.GENERAL)
                .couponIssuePeriod(30)
                .couponExpiredAt(LocalDateTime.now().minusDays(30))
                .build();

        when(couponPolicyRepository.findById(2L)).thenReturn(Optional.of(testCouponPolicy));

        CouponExpiredException exception = assertThrows(CouponExpiredException.class,
                () -> couponService.issueCouponToUser(100L, 2L));
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

        IssueBookCouponRequestDto requestDto = IssueBookCouponRequestDto.builder()
                .userId(100L)
                .couponPolicyId(1L)
                .bookId(100L)
                .build();

        CouponNotApplicableException ex = assertThrows(CouponNotApplicableException.class,
                () -> couponService.issueBookCoupon(requestDto));
        assertTrue(ex.getMessage().contains("해당 쿠폰 정책은 도서 전용 쿠폰이 아닙니다."));
    }

    @Test
    @DisplayName("웰컴 쿠폰 정상 발급")
    void testIssueWelcomeCoupon_Success() {
        Long userNo = 2L;
        UserCouponList welcomeCoupon = UserCouponList.builder()
                .userNo(userNo)
                .status(UserCouponStatus.ACTIVE)
                .expiredAt(LocalDateTime.now().plusDays(30))
                .build();

        when(couponPolicyRepository.findByCouponType(CouponType.WELCOME))
                .thenReturn(Optional.of(welcomeCouponPolicy));

        when(couponPolicyRepository.findById(anyLong()))
                .thenReturn(Optional.of(couponPolicy));

        when(userCouponListRepository.findByUserNoAndCouponPolicy(eq(userNo), any(CouponPolicy.class)))
                .thenReturn(List.of());

        when(userCouponListRepository.save(any())).thenReturn(welcomeCoupon);

        UserCouponList result = couponService.issueWelcomeCoupon(userNo);

        assertNotNull(result);
        assertEquals(userNo, result.getUserNo());
        assertEquals(UserCouponStatus.ACTIVE, result.getStatus());

        verify(couponPolicyRepository).findByCouponType(CouponType.WELCOME);
        verify(userCouponListRepository).findByUserNoAndCouponPolicy(eq(userNo), any(CouponPolicy.class));
        verify(userCouponListRepository).save(any());
    }

    @Test
    @DisplayName("웰컴 쿠폰 중복 발급 예외")
    void testIssueWelcomeCoupon_AlreadyExistException() {
        Long userNo = 20L;

        when(couponPolicyRepository.findByCouponType(CouponType.WELCOME))
                .thenReturn(Optional.of(welcomeCouponPolicy));

        when(userCouponListRepository.findByUserNoAndCouponPolicy(eq(userNo), any(CouponPolicy.class)))
                .thenReturn(List.of(new UserCouponList()));

        assertThrows(CouponAlreadyExistException.class,
                () -> couponService.issueWelcomeCoupon(userNo));
    }

    @Test
    @DisplayName("생일 쿠폰 정상 발급")
    void testIssueBirthdayCoupon_Success() {
        Long userNo = 30L;
        LocalDate userBirth = LocalDate.of(1990, 5, 10);

        UserCouponList birthdayCoupon = UserCouponList.builder()
                .userNo(userNo)
                .status(UserCouponStatus.ACTIVE)
                .expiredAt(LocalDateTime.now().plusDays(30))
                .build();

        // findByCouponType 목 설정만 남김
        when(couponPolicyRepository.findByCouponType(CouponType.BIRTHDAY))
                .thenReturn(Optional.of(birthCouponPolicy));

        // 기존 발급 쿠폰 중복체크 리스트는 빈 리스트로 설정 (중복없음)
        when(userCouponListRepository.findByUserNoAndCouponPolicy(eq(userNo), any(CouponPolicy.class)))
                .thenReturn(List.of());

        // 쿠폰 저장 시 그대로 반환
        when(userCouponListRepository.save(any())).thenReturn(birthdayCoupon);

        // 테스트 대상 실행
        UserCouponList result = couponService.issueBirthdayCoupon(userNo, userBirth);

        // 검증
        assertNotNull(result);
        assertEquals(userNo, result.getUserNo());

        // verify 호출 체크
        verify(couponPolicyRepository).findByCouponType(CouponType.BIRTHDAY);
        verify(userCouponListRepository).findByUserNoAndCouponPolicy(eq(userNo), any(CouponPolicy.class));
        verify(userCouponListRepository).save(any());
    }




    @Test
    @DisplayName("생일 쿠폰 중복 발급 예외")
    void testIssueBirthdayCoupon_AlreadyExistException() {
        Long userNo = 40L;
        LocalDate userBirth = LocalDate.of(1990, 5, 10);

        when(couponPolicyRepository.findByCouponType(CouponType.BIRTHDAY))
                .thenReturn(Optional.of(birthCouponPolicy));

        UserCouponList existingCoupon = UserCouponList.builder()
                .userNo(userNo)
                .couponPolicy(birthCouponPolicy)
                .issuedAt(LocalDateTime.now().withDayOfYear(1))
                .build();

        when(userCouponListRepository.findByUserNoAndCouponPolicy(eq(userNo), any(CouponPolicy.class)))
                .thenReturn(List.of(existingCoupon));

        CouponAlreadyExistException ex = assertThrows(CouponAlreadyExistException.class,
                () -> couponService.issueBirthdayCoupon(userNo, userBirth));

        assertTrue(ex.getMessage().contains("이번 연도 생일 쿠폰이 이미 발급되었습니다"));

        verify(couponPolicyRepository).findByCouponType(CouponType.BIRTHDAY);
        verify(userCouponListRepository).findByUserNoAndCouponPolicy(eq(userNo), any(CouponPolicy.class));
    }



    @Test
    @DisplayName("쿠폰 사용 정상")
    void testUseCoupon_Success() {
        Long userNo = 50L;
        Long userCouponId = 100L;
        Long orderId = 1L;

        UserCouponList userCoupon = UserCouponList.builder()
                .userCouponId(userCouponId)
                .userNo(userNo)
                .status(UserCouponStatus.ACTIVE)
                .expiredAt(LocalDateTime.now().plusDays(10))
                .build();

        when(userCouponListRepository.findByUserNoAndUserCouponId(userNo, userCouponId)).thenReturn(Optional.of(userCoupon));
        when(userCouponListRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        couponService.useCoupon(userNo, userCouponId, orderId);

        assertEquals(UserCouponStatus.USED, userCoupon.getStatus());
        assertEquals(orderId, userCoupon.getOrderId());
    }

    @Test
    @DisplayName("쿠폰 사용 - 쿠폰 미존재 예외")
    void testUseCoupon_NotFoundException() {
        Long userNo = 60L;
        Long userCouponId = 101L;
        Long orderId = 1L;

        when(userCouponListRepository.findByUserNoAndUserCouponId(userNo, userCouponId)).thenReturn(Optional.empty());

        assertThrows(UserCouponNotFoundException.class,
                () -> couponService.useCoupon(userNo, userCouponId, orderId));
    }

    @Test
    @DisplayName("쿠폰 사용 - 이미 사용된 쿠폰 예외")
    void testUseCoupon_CouponAlreadyUsedException() {
        Long userNo = 60L;
        Long userCouponId = 101L;
        Long orderId = 1L;

        UserCouponList usedCoupon = UserCouponList.builder()
                .userCouponId(userCouponId)
                .userNo(userNo)
                .status(UserCouponStatus.USED)
                .expiredAt(LocalDateTime.now().plusDays(5))
                .build();

        when(userCouponListRepository.findByUserNoAndUserCouponId(userNo, userCouponId))
                .thenReturn(Optional.of(usedCoupon));

        assertThrows(CouponAlreadyUsedException.class,
                () -> couponService.useCoupon(userNo, userCouponId, orderId));
    }

    @Test
    @DisplayName("쿠폰 사용 - 만료된 쿠폰 예외")
    void testUseCoupon_CouponExpiredException() {
        Long userNo = 60L;
        Long userCouponId = 102L;
        Long orderId = 2L;

        UserCouponList expiredCoupon1 = UserCouponList.builder()
                .userCouponId(userCouponId)
                .userNo(userNo)
                .status(UserCouponStatus.EXPIRED)
                .expiredAt(LocalDateTime.now().plusDays(5))
                .build();

        UserCouponList expiredCoupon2 = UserCouponList.builder()
                .userCouponId(userCouponId)
                .userNo(userNo)
                .status(UserCouponStatus.ACTIVE)
                .expiredAt(LocalDateTime.now().minusDays(1))
                .build();

        when(userCouponListRepository.findByUserNoAndUserCouponId(userNo, userCouponId))
                .thenReturn(Optional.of(expiredCoupon1));

        assertThrows(CouponExpiredException.class,
                () -> couponService.useCoupon(userNo, userCouponId, orderId));

        when(userCouponListRepository.findByUserNoAndUserCouponId(userNo, userCouponId))
                .thenReturn(Optional.of(expiredCoupon2));

        assertThrows(CouponExpiredException.class,
                () -> couponService.useCoupon(userNo, userCouponId, orderId));
    }


    @Test
    @DisplayName("주문 할인금액 계산 정상")
    void testCalculateDiscountAmount() {
        Long userNo = 70L;
        Long userCouponId = 200L;
        int orderAmount = 20000;
        List<Long> bookIds = List.of(1L, 2L);
        List<Long> categoryIds = List.of(10L);

        when(userCouponListRepository.findByUserNoAndUserCouponId(userNo, userCouponId))
                .thenReturn(Optional.of(UserCouponList.builder()
                        .userCouponId(userCouponId)
                        .userNo(userNo)
                        .couponPolicy(couponPolicy)
                        .status(UserCouponStatus.ACTIVE)
                        .expiredAt(LocalDateTime.now().plusDays(10))
                        .build()));

        int discountAmount = couponService.calculateDiscountAmount(userNo, userCouponId, orderAmount, bookIds, categoryIds);

        assertTrue(discountAmount >= 0);
    }

    @Test
    @DisplayName("쿠폰 정책 삭제")
    void testDeleteCouponPolicy() {
        Long couponPolicyId = 5L;

        when(couponPolicyRepository.findById(couponPolicyId))
                .thenReturn(Optional.of(couponPolicy));

        couponService.deleteCouponPolicy(couponPolicyId);

        verify(couponBookRepository).deleteByCouponPolicy(couponPolicy);
        verify(userCouponListRepository).deleteByCouponPolicy(couponPolicy);
        verify(couponPolicyRepository).delete(couponPolicy);
    }

    @Test
    @DisplayName("쿠폰 발급 프로세스 시작 - 정상 호출")
    void testStartCouponIssuingProcess() {
        Long couponPolicyId = 100L;

        when(couponPolicyRepository.findById(couponPolicyId)).thenReturn(Optional.of(couponPolicy));

        couponService.startCouponIssuingProcess(couponPolicyId);

        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), eq(couponPolicyId));
    }

    @Test
    @DisplayName("쿠폰 발급 프로세스 시작 - 정책 미존재 예외")
    void testStartCouponIssuingProcess_NotFound() {
        when(couponPolicyRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(CouponNotFoundException.class,
                () -> couponService.startCouponIssuingProcess(999L));
    }

    @Test
    @DisplayName("도서 쿠폰 발급 - 정상")
    void testIssueCouponToBook_Success() {
        Long bookId = 10L;

        when(couponPolicyRepository.findById(bookCouponPolicy.getCouponId())).thenReturn(Optional.of(bookCouponPolicy));
        when(couponBookRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        couponService.issueCouponToBook(bookCouponPolicy.getCouponId(), bookId);

        verify(couponPolicyRepository).findById(bookCouponPolicy.getCouponId());
        verify(couponBookRepository).save(any());
    }


    @Test
    @DisplayName("도서 쿠폰 발급 - 정책 미존재 예외")
    void testIssueCouponToBook_PolicyNotFound() {
        when(couponPolicyRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(CouponNotFoundException.class, () -> couponService.issueCouponToBook(999L, 10L));
    }

    @Test
    @DisplayName("도서 쿠폰 발급 - 스코프가 BOOK 아닐 경우 예외")
    void testIssueCouponToBook_NotBookScope() {

        when(couponPolicyRepository.findById(welcomeCouponPolicy.getCouponId())).thenReturn(Optional.of(welcomeCouponPolicy));

        Long couponPolicyId = welcomeCouponPolicy.getCouponId();

        assertThrows(CouponNotApplicableException.class,
                () -> couponService.issueCouponToBook(couponPolicyId, 10L));
    }

    @Test
    @DisplayName("유저 활성 쿠폰 리스트 조회")
    void testGetActiveUserCoupons() {
        Long userNo = 10L;
        UserCouponList coupon1 = UserCouponList.builder().userNo(userNo).build();
        UserCouponList coupon2 = UserCouponList.builder().userNo(userNo).build();

        when(userCouponListRepository.findActiveCouponsByUserNo(userNo))
                .thenReturn(List.of(coupon1, coupon2));

        List<UserCouponList> result = couponService.getActiveUserCoupons(userNo);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(c -> c.getUserNo().equals(userNo)));

        verify(userCouponListRepository).findActiveCouponsByUserNo(userNo);
    }

    @Test
    @DisplayName("유저 사용 쿠폰 리스트 조회")
    void testGetUsedUserCoupons() {
        Long userNo = 20L;
        UserCouponList coupon1 = UserCouponList.builder().userNo(userNo).build();

        when(userCouponListRepository.findUsedCouponsByUserNo(userNo))
                .thenReturn(List.of(coupon1));

        List<UserCouponList> result = couponService.getUsedUserCoupons(userNo);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userNo, result.getFirst().getUserNo());

        verify(userCouponListRepository).findUsedCouponsByUserNo(userNo);
    }

    @Test
    @DisplayName("쿠폰 정책 단일 조회 - 존재")
    void testGetCouponPolicy_Exists() {
        Long policyId = 100L;
        CouponPolicy mockPolicy = CouponPolicy.builder()
                .couponId(policyId)
                .couponName("Demo Coupon")
                .build();

        when(couponPolicyRepository.findById(policyId)).thenReturn(Optional.of(mockPolicy));

        CouponPolicy result = couponService.getCouponPolicy(policyId);

        assertNotNull(result);
        assertEquals(policyId, result.getCouponId());
        assertEquals("Demo Coupon", result.getCouponName());

        verify(couponPolicyRepository).findById(policyId);
    }

    @Test
    @DisplayName("쿠폰 정책 단일 조회 - 미존재 예외")
    void testGetCouponPolicy_NotExists() {
        Long policyId = 999L;

        when(couponPolicyRepository.findById(policyId)).thenReturn(Optional.empty());

        CouponNotFoundException exception = assertThrows(CouponNotFoundException.class,
                () -> couponService.getCouponPolicy(policyId));

        assertTrue(exception.getMessage().contains("쿠폰 정책을 찾을 수 없습니다"));

        verify(couponPolicyRepository).findById(policyId);
    }

    @Test
    @DisplayName("모든 쿠폰 정책 조회")
    void testGetAllCouponPolicies() {


        List<CouponPolicy> policies = List.of(couponPolicy, bookCouponPolicy, categoryCouponPolicy);

        when(couponPolicyRepository.findAll()).thenReturn(policies);

        // bookScopePolicy의 bookIds 반환 Stub
        when(couponBookRepository.findBookIdsByCouponId(bookCouponPolicy.getCouponId()))
                .thenReturn(List.of(101L, 102L));

        // categoryScopePolicy의 categoryIds 반환 Stub
        when(couponCategoryRepository.findCategoryIdsByCouponId(categoryCouponPolicy.getCouponId()))
                .thenReturn(List.of(201L, 202L));

        // allScopePolicy는 bookIds, categoryIds 없음 → no stub needed

        // 실행
        List<CouponPolicyResponseDto> results = couponService.getAllCouponPolicies();

        // 검증
        assertNotNull(results);
        assertEquals(3, results.size());

        // bookScopePolicy 검증
        CouponPolicyResponseDto bscp = results.stream()
                .filter(dto -> dto.getCouponId().equals(bookCouponPolicy.getCouponId()))
                .findFirst().orElse(null);
        assertNotNull(bscp);
        assertEquals(List.of(101L, 102L), bscp.getBookIds());
        assertNull(bscp.getCategoryIds());

        // categoryScopePolicy 검증
        CouponPolicyResponseDto cscp = results.stream()
                .filter(dto -> dto.getCouponId().equals(categoryCouponPolicy.getCouponId()))
                .findFirst().orElse(null);
        assertNotNull(cscp);
        assertEquals(List.of(201L, 202L), cscp.getCategoryIds());
        assertNull(cscp.getBookIds());

        // allScopePolicy 검증
        CouponPolicyResponseDto ascp = results.stream()
                .filter(dto -> dto.getCouponId().equals(couponPolicy.getCouponId()))
                .findFirst().orElse(null);
        assertNotNull(ascp);
        assertNull(ascp.getBookIds());
        assertNull(ascp.getCategoryIds());

        verify(couponPolicyRepository).findAll();
        verify(couponBookRepository).findBookIdsByCouponId(bookCouponPolicy.getCouponId());
        verify(couponCategoryRepository).findCategoryIdsByCouponId(categoryCouponPolicy.getCouponId());
    }


}
