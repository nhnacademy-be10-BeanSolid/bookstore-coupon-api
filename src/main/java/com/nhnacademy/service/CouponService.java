package com.nhnacademy.service;

import com.nhnacademy.common.config.RabbitMQConfig;
import com.nhnacademy.common.exception.CouponAlreadyExistException;
import com.nhnacademy.exception.*;
import com.nhnacademy.domain.*;
import com.nhnacademy.dto.CouponPolicyResponseDto;
import com.nhnacademy.repository.CouponBookRepository;
import com.nhnacademy.repository.CouponCategoryRepository;
import com.nhnacademy.repository.CouponPolicyRepository;
import com.nhnacademy.repository.UserCouponRepository;
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
@Transactional(readOnly = true)
public class CouponService {

    private final CouponPolicyRepository couponPolicyRepository;
    private final UserCouponRepository userCouponRepository;
    private final CouponBookRepository couponBookRepository;
    private final CouponCategoryRepository couponCategoryRepository;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public CouponPolicy createCouponPolicy(String name, CouponDiscountType discountType, int discountAmount,
                                           Integer minOrderAmount, Integer maxDiscountAmount,
                                           CouponScope scope, LocalDateTime expiredAt, Integer issuePeriod,
                                           List<Long> bookIds, List<Long> categoryIds, CouponType couponType) {
        CouponPolicy policy = CouponPolicy.builder()
                .couponName(name)
                .couponDiscountType(discountType)
                .couponDiscountAmount(discountAmount)
                .couponMinimumOrderAmount(minOrderAmount)
                .couponMaximumDiscountAmount(maxDiscountAmount)
                .couponScope(scope)
                .couponExpiredAt(expiredAt)
                .couponIssuePeriod(issuePeriod)
                .couponType(couponType)
                .build();
        CouponPolicy savedPolicy = couponPolicyRepository.save(policy);

        if (scope == CouponScope.BOOK && bookIds != null && !bookIds.isEmpty()) {
            bookIds.forEach(bookId -> couponBookRepository.save(CouponBook.builder()
                    .couponId(savedPolicy.getCouponId())
                    .bookId(bookId)
                    .couponPolicy(savedPolicy)
                    .build()));
        } else if (scope == CouponScope.CATEGORY && categoryIds != null && !categoryIds.isEmpty()) {
            categoryIds.forEach(categoryId -> couponCategoryRepository.save(CouponCategory.builder()
                    .couponId(savedPolicy.getCouponId())
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
    public UsedCoupon issueCouponToUser(Long userNo, Long couponPolicyId) {
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

        UsedCoupon usedCoupon = UsedCoupon.builder()
                .userNo(userNo)
                .couponPolicy(policy)
                .issuedAt(LocalDateTime.now())
                .expiredAt(userCouponExpiredAt)
                .status(UserCouponStatus.ACTIVE)
                .build();

        return userCouponRepository.save(usedCoupon);
    }

    @Transactional
    public UsedCoupon issueBookCoupon(com.nhnacademy.dto.IssueBookCouponRequest request) {
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

    public List<UsedCoupon> getActiveUserCoupons(Long userNo) {
        return userCouponRepository.findActiveCouponsByUserIdAndPeriod(userNo, LocalDateTime.now().minusYears(100), LocalDateTime.now().plusYears(100));
    }

    public List<UsedCoupon> getUsedUserCoupons(Long userNo) {
        return userCouponRepository.findUsedCouponsByUserId(userNo);
    }

    public List<UsedCoupon> getExpiredUserCoupons(Long userNo) {
        return userCouponRepository.findExpiredCouponsByUserId(userNo);
    }

    public CouponPolicy getCouponPolicy(Long policyId) {
        return couponPolicyRepository.findById(policyId)
                .orElseThrow(() -> new CouponNotFoundException("쿠폰 정책을 찾을 수 없습니다: " + policyId));
    }

    @Transactional
    public UsedCoupon issueWelcomeCoupon(Long userNo) {
        log.info("Attempting to find welcome coupon policy by type: WELCOME");
        CouponPolicy welcomePolicy = couponPolicyRepository.findByCouponType(CouponType.WELCOME)
                .orElseThrow(() -> new WelcomeCouponPolicyNotFoundException("Welcome 쿠폰 정책을 찾을 수 없습니다."));
        List<UsedCoupon> existingWelcomeCoupons = userCouponRepository.findByUserNoAndCouponPolicy(userNo, welcomePolicy);

        if (!existingWelcomeCoupons.isEmpty()) {
            throw new CouponAlreadyExistException(String.format("사용자 ID: %s에게 웰컴 쿠폰이 이미 발급되었습니다.", userNo));
        }
        return issueCouponToUser(userNo, welcomePolicy.getCouponId());
    }

    @Transactional
    public UsedCoupon issueBirthdayCoupon(Long userNo, LocalDate userBirth) {
        log.info("Attempting to find birthday coupon policy by type: BIRTHDAY");
        CouponPolicy birthdayPolicy = couponPolicyRepository.findByCouponType(CouponType.BIRTHDAY)
                .orElseThrow(() -> new CouponNotFoundException("Birthday 쿠폰 정책을 찾을 수 없습니다."));

        boolean alreadyIssuedThisYear = userCouponRepository.findByUserNoAndCouponPolicy(userNo, birthdayPolicy)
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
        UsedCoupon usedCoupon = UsedCoupon.builder()
                .userNo(userNo)
                .couponPolicy(birthdayPolicy)
                .issuedAt(now)
                .expiredAt(expiredAtCandidate)
                .status(UserCouponStatus.ACTIVE)
                .build();

        return userCouponRepository.save(usedCoupon);
    }

    @Transactional
    public void useCoupon(Long userNo, Long userCouponId, Long orderId) {
        UsedCoupon usedCoupon = userCouponRepository.findByUserNoAndUserCouponId(userNo, userCouponId)
                .orElseThrow(() -> new UserCouponNotFoundException("쿠폰을 찾을 수 없습니다. UserCoupon ID: " + userCouponId + " 또는 사용자 ID: " + userNo + "와 일치하지 않습니다."));

        if (usedCoupon.getStatus() == UserCouponStatus.USED) {
            throw new CouponAlreadyUsedException("이미 사용된 쿠폰입니다.");
        }
        if (usedCoupon.getStatus() == UserCouponStatus.EXPIRED || usedCoupon.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new CouponExpiredException("만료된 쿠폰입니다.");
        }

        usedCoupon.use();
        usedCoupon.setOrderId(orderId);
        userCouponRepository.save(usedCoupon);
    }

    public Integer calculateDiscountAmount(Long userNo, Long userCouponId, int orderAmount, List<Long> bookIdsInOrder, List<Long> categoryIdsInOrder) {
        UsedCoupon usedCoupon = userCouponRepository.findByUserNoAndUserCouponId(userNo, userCouponId)
                .orElseThrow(() -> new UserCouponNotFoundException("쿠폰을 찾을 수 없습니다. UserCoupon ID: " + userCouponId + " 또는 사용자 ID: " + userNo + "와 일치하지 않습니다."));

        if (usedCoupon.getStatus() != UserCouponStatus.ACTIVE) {
            throw new CouponNotApplicableException("사용할 수 없는 쿠폰입니다. (ACTIVE 상태가 아님)");
        }
        if (usedCoupon.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new CouponExpiredException("만료된 쿠폰입니다. 할인 계산에 사용할 수 없습니다.");
        }

        CouponPolicy policy = usedCoupon.getCouponPolicy();

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

        int discount = 0;
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

    @Transactional
    public void issueCouponToBook(Long couponPolicyId, Long bookId) {
        CouponPolicy policy = couponPolicyRepository.findById(couponPolicyId)
                .orElseThrow(() -> new CouponNotFoundException("존재하지 않는 쿠폰 정책입니다. Policy ID: " + couponPolicyId));

        if (policy.getCouponScope() != CouponScope.BOOK) {
            throw new CouponNotApplicableException("해당 쿠폰 정책은 도서 전용 쿠폰이 아닙니다.");
        }

        CouponBook couponBook = CouponBook.builder()
                .couponId(policy.getCouponId())
                .bookId(bookId)
                .couponPolicy(policy)
                .build();
        couponBookRepository.save(couponBook);
        log.info("Coupon policy {} associated with book {}.", couponPolicyId, bookId);
    }
}
