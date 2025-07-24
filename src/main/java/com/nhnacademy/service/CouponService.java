package com.nhnacademy.service;

import com.nhnacademy.domain.CouponPolicy;
import com.nhnacademy.domain.UserCouponList;
import com.nhnacademy.dto.request.CouponPolicyRequestDto;
import com.nhnacademy.dto.request.IssueBookCouponRequestDto;
import com.nhnacademy.dto.response.CouponPolicyResponseDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CouponService {

    CouponPolicy createCouponPolicy(CouponPolicyRequestDto request);

    List<CouponPolicyResponseDto> getAllCouponPolicies();

    Optional<CouponPolicy> getCouponPolicyById(Long couponId);

    UserCouponList issueCouponToUser(Long userNo, Long couponPolicyId);

    UserCouponList issueBookCoupon(IssueBookCouponRequestDto request);

    List<UserCouponList> getActiveUserCoupons(Long userNo);

    List<UserCouponList> getUsedUserCoupons(Long userNo);

    

    CouponPolicy getCouponPolicy(Long policyId);

    UserCouponList issueWelcomeCoupon(Long userNo);

    UserCouponList issueBirthdayCoupon(Long userNo, LocalDate userBirth);

    List<Long> getBookIdsByCouponId(Long couponId);

    List<Long> getCategoryIdsByCouponId(Long couponId);

    void useCoupon(Long userNo, Long userCouponId, Long orderId);

    void deleteCouponPolicy(Long couponId);

    Integer calculateDiscountAmount(Long userNo, Long userCouponId, int orderAmount, List<Long> bookIdsInOrder, List<Long> categoryIdsInOrder);

    void startCouponIssuingProcess(Long couponPolicyId);

    void issueCouponToBook(Long couponPolicyId, Long bookId);

    UserCouponList issueCategoryCoupon(Long userNo, Long couponPolicyId, Long categoryId);
}
