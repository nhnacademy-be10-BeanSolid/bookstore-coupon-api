package com.nhnacademy.service;

import com.nhnacademy.domain.CouponBook;
import com.nhnacademy.domain.CouponCategory;
import com.nhnacademy.domain.CouponDiscountType;
import com.nhnacademy.domain.CouponPolicy;
import com.nhnacademy.domain.CouponScope;
import com.nhnacademy.domain.UsedCoupon;
import com.nhnacademy.domain.UserCouponStatus;
import com.nhnacademy.exception.CouponAlreadyUsedException;
import com.nhnacademy.exception.CouponExpiredException;
import com.nhnacademy.exception.CouponNotApplicableException;
import com.nhnacademy.exception.CouponNotFoundException;
import com.nhnacademy.exception.UserCouponNotFoundException;
import com.nhnacademy.exception.WelcomeCouponPolicyNotFoundException;
import com.nhnacademy.repository.CouponBookRepository;
import com.nhnacademy.repository.CouponCategoryRepository;
import com.nhnacademy.repository.CouponPolicyRepository;
import com.nhnacademy.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponService {

    private final CouponPolicyRepository couponPolicyRepository;
    private final UserCouponRepository userCouponRepository;
    private final CouponBookRepository couponBookRepository;
    private final CouponCategoryRepository couponCategoryRepository;

    @Transactional
    public CouponPolicy createCouponPolicy(String name, CouponDiscountType discountType, int discountAmount,
                                           Integer minOrderAmount, Integer maxDiscountAmount,
                                           CouponScope scope, LocalDateTime expiredAt, Integer issuePeriod,
                                           List<Long> bookIds, List<Long> categoryIds) {
        CouponPolicy policy = CouponPolicy.builder()
                .couponName(name)
                .couponDiscountType(discountType)
                .couponDiscountAmount(discountAmount)
                .couponMinimumOrderAmount(minOrderAmount)
                .couponMaximumDiscountAmount(maxDiscountAmount)
                .couponScope(scope)
                .couponExpiredAt(expiredAt)
                .couponIssuePeriod(issuePeriod)
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

    public List<CouponPolicy> getAllCouponPolicies() {
        return couponPolicyRepository.findAll();
    }

    public Optional<CouponPolicy> getCouponPolicyById(Long couponId) {
        return couponPolicyRepository.findById(couponId);
    }

    @Transactional
    public UsedCoupon issueCouponToUser(String userId, Long couponPolicyId) {
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
                .userId(userId)
                .couponPolicy(policy)
                .issuedAt(LocalDateTime.now())
                .expiredAt(userCouponExpiredAt)
                .status(UserCouponStatus.ACTIVE)
                .build();

        return userCouponRepository.save(usedCoupon);
    }

    public List<UsedCoupon> getUserCoupons(String userId) {
        return userCouponRepository.findByUserId(userId);
    }

    public CouponPolicy getCouponPolicy(Long policyId) {
        return couponPolicyRepository.findById(policyId)
                .orElseThrow(() -> new CouponNotFoundException("쿠폰 정책을 찾을 수 없습니다: " + policyId));
    }

    @Transactional
    public UsedCoupon issueWelcomeCoupon(String userId) {
        CouponPolicy welcomePolicy = couponPolicyRepository.findByCouponName("Welcome Coupon")
                .orElseThrow(() -> new WelcomeCouponPolicyNotFoundException("Welcome 쿠폰 정책을 찾을 수 없습니다."));
        List<UsedCoupon> existingWelcomeCoupons = userCouponRepository.findByUserIdAndCouponPolicy(userId, welcomePolicy);

        if (!existingWelcomeCoupons.isEmpty()) {
            throw new IllegalStateException(String.format("사용자 ID: %s에게 웰컴 쿠폰이 이미 발급되었습니다.", userId));
        }
        return issueCouponToUser(userId, welcomePolicy.getCouponId());
    }

    @Transactional
    public UsedCoupon issueBirthdayCoupon(String userId, LocalDate userBirth) {
        CouponPolicy birthdayPolicy = couponPolicyRepository.findByCouponName("Birthday Coupon")
                .orElseThrow(() -> new CouponNotFoundException("Birthday 쿠폰 정책을 찾을 수 없습니다."));

        boolean alreadyIssuedThisYear = userCouponRepository.findByUserIdAndCouponPolicy(userId, birthdayPolicy)
                .stream()
                .anyMatch(uc -> uc.getIssuedAt().getYear() == LocalDateTime.now().getYear());

        if (alreadyIssuedThisYear) {
            throw new IllegalStateException(String.format("사용자 ID: %s에게 이번 연도 생일 쿠폰이 이미 발급되었습니다.", userId));
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
                .userId(userId)
                .couponPolicy(birthdayPolicy)
                .issuedAt(now)
                .expiredAt(expiredAtCandidate)
                .status(UserCouponStatus.ACTIVE)
                .build();

        return userCouponRepository.save(usedCoupon);
    }

    @Transactional
    public void useCoupon(String userId, Long userCouponId, Long orderId) {
        UsedCoupon usedCoupon = userCouponRepository.findByUserIdAndUserCouponId(userId, userCouponId)
                .orElseThrow(() -> new UserCouponNotFoundException("쿠폰을 찾을 수 없습니다. UserCoupon ID: " + userCouponId + " 또는 사용자 ID: " + userId + "와 일치하지 않습니다."));

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

    public Integer calculateDiscountAmount(String userId, Long userCouponId, int orderAmount, List<Long> bookIdsInOrder, List<Long> categoryIdsInOrder) {
        UsedCoupon usedCoupon = userCouponRepository.findByUserIdAndUserCouponId(userId, userCouponId)
                .orElseThrow(() -> new UserCouponNotFoundException("쿠폰을 찾을 수 없습니다. UserCoupon ID: " + userCouponId + " 또는 사용자 ID: " + userId + "와 일치하지 않습니다."));

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
            List<Long> applicableBookIds = couponBookRepository.findByCouponId(policy.getCouponId())
                    .stream().map(CouponBook::getBookId).collect(Collectors.toList());

            if (bookIdsInOrder == null || bookIdsInOrder.isEmpty() || !bookIdsInOrder.stream().anyMatch(applicableBookIds::contains)) {
                throw new CouponNotApplicableException("주문에 쿠폰 적용 대상 도서가 포함되어 있지 않습니다.");
            }
        } else if (policy.getCouponScope() == CouponScope.CATEGORY) {
            List<Long> applicableCategoryIds = couponCategoryRepository.findByCouponId(policy.getCouponId())
                    .stream().map(CouponCategory::getCategoryId).collect(Collectors.toList());

            if (categoryIdsInOrder == null || categoryIdsInOrder.isEmpty() || !categoryIdsInOrder.stream().anyMatch(applicableCategoryIds::contains)) {
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
}