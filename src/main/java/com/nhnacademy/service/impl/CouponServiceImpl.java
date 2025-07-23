package com.nhnacademy.service.impl;

import com.nhnacademy.common.config.RabbitMQConfig;
import com.nhnacademy.common.exception.CouponAlreadyExistException;
import com.nhnacademy.domain.enumtype.CouponDiscountType;
import com.nhnacademy.domain.enumtype.CouponScope;
import com.nhnacademy.domain.enumtype.CouponType;
import com.nhnacademy.domain.enumtype.UserCouponStatus;
import com.nhnacademy.dto.request.CouponPolicyRequestDto;
import com.nhnacademy.dto.request.IssueBookCouponRequestDto;
import com.nhnacademy.exception.*;
import com.nhnacademy.domain.*;
import com.nhnacademy.dto.response.CouponPolicyResponseDto;
import com.nhnacademy.repository.CouponBookRepository;
import com.nhnacademy.repository.CouponCategoryRepository;
import com.nhnacademy.repository.CouponPolicyRepository;
import com.nhnacademy.repository.UserCouponListRepository;
import com.nhnacademy.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CouponServiceImpl implements CouponService {

    private final CouponPolicyRepository couponPolicyRepository;
    private final UserCouponListRepository userCouponListRepository;
    private final CouponBookRepository couponBookRepository;
    private final CouponCategoryRepository couponCategoryRepository;
    private final RabbitTemplate rabbitTemplate;

    public CouponPolicy createCouponPolicy(CouponPolicyRequestDto request) {
        CouponPolicy policy = CouponPolicy.builder()
                .couponName(request.getCouponName())
                .couponDiscountType(request.getCouponDiscountType())
                .couponDiscountAmount(request.getCouponDiscountAmount())
                .couponMinimumOrderAmount(request.getCouponMinimumOrderAmount())
                .couponMaximumDiscountAmount(request.getCouponMaximumDiscountAmount())
                .couponScope(request.getCouponScope())
                .couponExpiredAt(request.getCouponExpiredAt())
                .couponIssuePeriod(request.getCouponIssuePeriod())
                .couponType(request.getCouponType())
                .build();
        CouponPolicy savedPolicy = couponPolicyRepository.save(policy);

        if (request.getCouponScope() == CouponScope.BOOK && request.getBookIds() != null && !request.getBookIds().isEmpty()) {
            request.getBookIds().forEach(bookId -> couponBookRepository.save(CouponBook.builder()
                    .couponBookId(savedPolicy.getCouponId())
                    .bookId(bookId)
                    .couponPolicy(savedPolicy)
                    .build()));
        } else if (request.getCouponScope() == CouponScope.CATEGORY && request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            request.getCategoryIds().forEach(categoryId -> couponCategoryRepository.save(CouponCategory.builder()
                    .categoryId(categoryId)
                    .couponPolicy(savedPolicy)
                    .build()));
        }
        return savedPolicy;
    }

    public List<CouponPolicyResponseDto> getAllCouponPolicies() {
        return couponPolicyRepository.findAll().stream().map(policy -> {
            List<Long> bookIds = null;
            List<Long> categoryIds = null;
            if (policy.getCouponScope() == CouponScope.BOOK) {
                bookIds = couponBookRepository.findBookIdsByCouponId(policy.getCouponId());
            } else if (policy.getCouponScope() == CouponScope.CATEGORY) {
                categoryIds = couponCategoryRepository.findCategoryIdsByCouponId(policy.getCouponId());
            }
            return CouponPolicyResponseDto.builder()
                    .couponId(policy.getCouponId())
                    .couponName(policy.getCouponName())
                    .couponDiscountType(policy.getCouponDiscountType())
                    .couponDiscountAmount(policy.getCouponDiscountAmount())
                    .couponMinimumOrderAmount(policy.getCouponMinimumOrderAmount())
                    .couponMaximumDiscountAmount(policy.getCouponMaximumDiscountAmount())
                    .couponScope(policy.getCouponScope())
                    .couponExpiredAt(policy.getCouponExpiredAt())
                    .couponIssuePeriod(policy.getCouponIssuePeriod())
                    .couponType(policy.getCouponType())
                    .couponCreatedAt(policy.getCouponCreatedAt())
                    .bookIds(bookIds)
                    .categoryIds(categoryIds)
                    .build();
        }).collect(Collectors.toList());
    }

    public Optional<CouponPolicy> getCouponPolicyById(Long couponId) {
        return couponPolicyRepository.findById(couponId);
    }

    @Transactional
    public UserCouponList issueCouponToUser(Long userNo, Long couponPolicyId) {
        CouponPolicy policy = couponPolicyRepository.findById(couponPolicyId)
                .orElseThrow(() -> new CouponNotFoundException("존재하지 않는 쿠폰 정책입니다. Policy ID: " + couponPolicyId));

        if (policy.getCouponExpiredAt() != null && policy.getCouponExpiredAt().isBefore(LocalDateTime.now())) {
            throw new CouponExpiredException("만료된 쿠폰 정책입니다. 이 정책으로는 쿠폰을 발급할 수 없습니다.");
        }

        LocalDateTime userCouponExpiredAt;
        if (policy.getCouponIssuePeriod() != null) {
            userCouponExpiredAt = LocalDateTime.now().plusDays(policy.getCouponIssuePeriod());
        } else if (policy.getCouponExpiredAt() != null) {
            userCouponExpiredAt = policy.getCouponExpiredAt();
        } else {
            userCouponExpiredAt = LocalDateTime.now().plusDays(365);
        }

        UserCouponList userCoupon = UserCouponList.builder()
                .userNo(userNo)
                .couponPolicy(policy)
                .issuedAt(LocalDateTime.now())
                .expiredAt(userCouponExpiredAt)
                .status(UserCouponStatus.ACTIVE)
                .build();

        return userCouponListRepository.save(userCoupon);
    }

    public UserCouponList issueBookCoupon(IssueBookCouponRequestDto request) {
        CouponPolicy policy = couponPolicyRepository.findById(request.getCouponPolicyId())
                .orElseThrow(() -> new CouponNotFoundException("존재하지 않는 쿠폰 정책입니다. Policy ID: " + request.getCouponPolicyId()));

        if (policy.getCouponScope() != CouponScope.BOOK) {
            throw new CouponNotApplicableException("해당 쿠폰 정책은 도서 전용 쿠폰이 아닙니다.");
        }

        boolean bookCouponExists = couponBookRepository.existsByCouponPolicy_CouponIdAndBookId(request.getCouponPolicyId(), request.getBookId());
        if (!bookCouponExists) {
            throw new CouponNotApplicableException("해당 도서에 적용되는 쿠폰 정책이 아닙니다. Policy ID: " + request.getCouponPolicyId() + ", Book ID: " + request.getBookId());
        }

        return issueCouponToUser(request.getUserId(), request.getCouponPolicyId());
    }

    public List<UserCouponList> getActiveUserCoupons(Long userNo) {
        return userCouponListRepository.findActiveCouponsByUserNo(userNo);
    }

    public List<UserCouponList> getUsedUserCoupons(Long userNo) {
        return userCouponListRepository.findUsedCouponsByUserNo(userNo);
    }

    

    public CouponPolicy getCouponPolicy(Long policyId) {
        return couponPolicyRepository.findById(policyId)
                .orElseThrow(() -> new CouponNotFoundException("쿠폰 정책을 찾을 수 없습니다: " + policyId));
    }

    public UserCouponList issueWelcomeCoupon(Long userNo) {
        log.info("Attempting to find welcome coupon policy by type: WELCOME");
        CouponPolicy welcomePolicy = couponPolicyRepository.findByCouponType(CouponType.WELCOME)
                .orElseThrow(() -> new WelcomeCouponPolicyNotFoundException("Welcome 쿠폰 정책을 찾을 수 없습니다."));
        List<UserCouponList> existingWelcomeCoupons = userCouponListRepository.findByUserNoAndCouponPolicy(userNo, welcomePolicy);

        if (!existingWelcomeCoupons.isEmpty()) {
            throw new CouponAlreadyExistException(String.format("사용자 ID: %s에게 웰컴 쿠폰이 이미 발급되었습니다.", userNo));
        }
        return issueCouponToUser(userNo, welcomePolicy.getCouponId());
    }

    public UserCouponList issueBirthdayCoupon(Long userNo, LocalDate userBirth) {
        log.info("Attempting to find birthday coupon policy by type: BIRTHDAY");
        CouponPolicy birthdayPolicy = couponPolicyRepository.findByCouponType(CouponType.BIRTHDAY)
                .orElseThrow(() -> new CouponNotFoundException("Birthday 쿠폰 정책을 찾을 수 없습니다."));

        boolean alreadyIssuedThisYear = userCouponListRepository.findByUserNoAndCouponPolicy(userNo, birthdayPolicy)
                .stream()
                .anyMatch(uc -> uc.getIssuedAt().getYear() == LocalDateTime.now().getYear());

        if (alreadyIssuedThisYear) {
            throw new CouponAlreadyExistException(String.format("사용자 ID: %s에게 이번 연도 생일 쿠폰이 이미 발급되었습니다.", userNo));
        }

        LocalDateTime now = LocalDateTime.now();
        int birthMonth = userBirth.getMonthValue();
        LocalDateTime expiredAtCandidate = now.withMonth(birthMonth)
                .with(TemporalAdjusters.lastDayOfMonth())
                .toLocalDate().atTime(23, 59, 59);

        if (expiredAtCandidate.isBefore(now)) {
            expiredAtCandidate = expiredAtCandidate.plusYears(1);
        }
        UserCouponList userCoupon = UserCouponList.builder()
                .userNo(userNo)
                .couponPolicy(birthdayPolicy)
                .issuedAt(now)
                .expiredAt(expiredAtCandidate)
                .status(UserCouponStatus.ACTIVE)
                .build();

        return userCouponListRepository.save(userCoupon);
    }

    public List<Long> getBookIdsByCouponId(Long couponId) {
        return couponBookRepository.findBookIdsByCouponId(couponId);
    }

    public List<Long> getCategoryIdsByCouponId(Long couponId) {
        return couponCategoryRepository.findCategoryIdsByCouponId(couponId);
    }

    public void useCoupon(Long userNo, Long userCouponId, Long orderId) {
        UserCouponList userCoupon = userCouponListRepository.findByUserNoAndUserCouponId(userNo, userCouponId)
                .orElseThrow(() -> new UserCouponNotFoundException("쿠폰을 찾을 수 없습니다. UserCoupon ID: " + userCouponId + " 또는 사용자 ID: " + userNo + "와 일치하지 않습니다."));

        if (userCoupon.getStatus() == UserCouponStatus.USED) {
            throw new CouponAlreadyUsedException("이미 사용된 쿠폰입니다.");
        }
        if (userCoupon.getStatus() == UserCouponStatus.EXPIRED || userCoupon.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new CouponExpiredException("만료된 쿠폰입니다.");
        }

        userCoupon.use();
        userCoupon.setOrderId(orderId);
        userCouponListRepository.save(userCoupon);
    }

    public void deleteCouponPolicy(Long couponId) {
        CouponPolicy policy = couponPolicyRepository.findById(couponId)
                .orElseThrow(() -> new CouponNotFoundException("존재하지 않는 쿠폰 정책입니다. Policy ID: " + couponId));
        couponBookRepository.deleteByCouponPolicy(policy); // 연결된 CouponBook 먼저 삭제
        userCouponListRepository.deleteByCouponPolicy(policy); // 연결된 UsedCoupon 먼저 삭제
        couponPolicyRepository.delete(policy);
    }

    public Integer calculateDiscountAmount(Long userNo, Long userCouponId, int orderAmount, List<Long> bookIdsInOrder, List<Long> categoryIdsInOrder) {
        UserCouponList userCoupon = userCouponListRepository.findByUserNoAndUserCouponId(userNo, userCouponId)
                .orElseThrow(() -> new UserCouponNotFoundException("쿠폰을 찾을 수 없습니다. UserCoupon ID: " + userCouponId + " 또는 사용자 ID: " + userNo + "와 일치하지 않습니다."));

        if (userCoupon.getStatus() != UserCouponStatus.ACTIVE) {
            throw new CouponNotApplicableException("사용할 수 없는 쿠폰입니다. (ACTIVE 상태가 아님)");
        }
        if (userCoupon.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new CouponExpiredException("만료된 쿠폰입니다. 할인 계산에 사용할 수 없습니다.");
        }

        CouponPolicy policy = userCoupon.getCouponPolicy();

        if (policy.getCouponMinimumOrderAmount() != null && orderAmount < policy.getCouponMinimumOrderAmount()) {
            throw new CouponNotApplicableException(
                    String.format("최소 주문 금액 %d원 이상이어야 사용 가능합니다.", policy.getCouponMinimumOrderAmount()));
        }

        if (policy.getCouponScope() == CouponScope.BOOK) {
            boolean isApplicable = couponBookRepository.existsByCouponPolicyIdAndBookIdsIn(policy.getCouponId(), bookIdsInOrder);
            if (!isApplicable) {
                throw new CouponNotApplicableException("주문에 쿠폰 적용 대상 도서가 포함되어 있지 않습니다.");
            }
        } else if (policy.getCouponScope() == CouponScope.CATEGORY) {
            boolean isApplicable = couponCategoryRepository.existsByCouponPolicyIdAndCategoryIdsIn(policy.getCouponId(), categoryIdsInOrder);
            if (!isApplicable) {
                throw new CouponNotApplicableException("주문에 쿠폰 적용 대상 카테고리가 포함되어 있지 않습니다.");
            }
        }

        int discount;
        if (policy.getCouponDiscountType() == CouponDiscountType.AMOUNT) {
            discount = policy.getCouponDiscountAmount();
        } else if (policy.getCouponDiscountType() == CouponDiscountType.PERCENT) {
            discount = (int) (orderAmount * (policy.getCouponDiscountAmount() / 100.0));
            if (policy.getCouponMaximumDiscountAmount() != null && discount > policy.getCouponMaximumDiscountAmount()) {
                discount = policy.getCouponMaximumDiscountAmount();
            }
        } else {
            throw new CouponNotApplicableException("지원하지 않는 쿠폰 할인 유형입니다.");
        }
        return discount;
    }

    public void startCouponIssuingProcess(Long couponPolicyId) {
        couponPolicyRepository.findById(couponPolicyId)
                .orElseThrow(() -> new CouponNotFoundException("존재하지 않는 쿠폰 정책입니다. Policy ID: " + couponPolicyId));

        log.info("Publishing CouponIssuingStartedEvent for couponPolicyId: {}", couponPolicyId);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.COUPON_ISSUING_STARTED_EXCHANGE,
                RabbitMQConfig.COUPON_ISSUING_STARTED_ROUTING_KEY,
                couponPolicyId
        );
    }

    public void issueCouponToBook(Long couponPolicyId, Long bookId) {
        CouponPolicy policy = couponPolicyRepository.findById(couponPolicyId)
                .orElseThrow(() -> new CouponNotFoundException("존재하지 않는 쿠폰 정책입니다. Policy ID: " + couponPolicyId));

        if (policy.getCouponScope() != CouponScope.BOOK) {
            throw new CouponNotApplicableException("해당 쿠폰 정책은 도서 전용 쿠폰이 아닙니다.");
        }

        CouponBook couponBook = CouponBook.builder()
                .couponBookId(policy.getCouponId())
                .bookId(bookId)
                .couponPolicy(policy)
                .build();
        couponBookRepository.save(couponBook);
        log.info("Coupon policy {} associated with book {}.", couponPolicyId, bookId);
    }
}
