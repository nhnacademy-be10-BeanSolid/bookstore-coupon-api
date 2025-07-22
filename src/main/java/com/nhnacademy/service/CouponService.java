package com.nhnacademy.service;

import com.nhnacademy.domain.CouponPolicy;
import com.nhnacademy.domain.UserCoupon;
import com.nhnacademy.dto.request.CouponPolicyRequest;
import com.nhnacademy.dto.request.IssueBookCouponRequest;
import com.nhnacademy.dto.response.CouponPolicyResponseDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CouponService {

    CouponPolicy createCouponPolicy(CouponPolicyRequest request);

    List<CouponPolicyResponseDto> getAllCouponPolicies();

    Optional<CouponPolicy> getCouponPolicyById(Long couponId);

    UserCoupon issueCouponToUser(Long userNo, Long couponPolicyId);

    UserCoupon issueBookCoupon(IssueBookCouponRequest request);

    List<UserCoupon> getActiveUserCoupons(Long userNo);

    List<UserCoupon> getUsedUserCoupons(Long userNo);

    List<UserCoupon> getExpiredUserCoupons(Long userNo);

    CouponPolicy getCouponPolicy(Long policyId);

    UserCoupon issueWelcomeCoupon(Long userNo);

    UserCoupon issueBirthdayCoupon(Long userNo, LocalDate userBirth);

    List<Long> getBookIdsByCouponId(Long couponId);

    List<Long> getCategoryIdsByCouponId(Long couponId);

    void useCoupon(Long userNo, Long userCouponId, Long orderId);

    void deleteCouponPolicy(Long couponId);

    Integer calculateDiscountAmount(Long userNo, Long userCouponId, int orderAmount, List<Long> bookIdsInOrder, List<Long> categoryIdsInOrder);

    void startCouponIssuingProcess(Long couponPolicyId);

    void issueCouponToBook(Long couponPolicyId, Long bookId);
}
