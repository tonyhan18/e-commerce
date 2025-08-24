package kr.hhplus.be.server.application.user;

import java.util.List;

import kr.hhplus.be.server.domain.coupon.CouponInfo;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.user.UserCouponInfo;
import kr.hhplus.be.server.domain.user.UserCouponService;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.domain.user.UserCouponCommand;
import kr.hhplus.be.server.support.lock.DistributedLock;
import kr.hhplus.be.server.support.lock.LockStrategy;
import kr.hhplus.be.server.support.lock.LockType;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserCouponFacade {

    private final UserService userService;
    private final CouponService couponService;
    private final UserCouponService userCouponService;

    public void requestPublishUserCoupon(UserCouponCriteria.PublishRequest criteria) {
        userCouponService.requestPublishUserCoupon(criteria.toCommand(LocalDateTime.now()));
    }

    @Transactional
    public void publishUserCoupons(UserCouponCriteria.Publish criteria) {
        CouponInfo.PublishableCoupons coupons = couponService.getPublishableCoupons();

        coupons.getCoupons().stream()
            .map(p -> criteria.toCommand(p.getCouponId(), p.getQuantity()))
            .forEach(userCouponService::publishUserCoupons);
    }

    @Transactional
    public void finishedPublishCoupons() {
        CouponInfo.PublishableCoupons coupons = couponService.getPublishableCoupons();

        coupons.getCoupons().stream()
            .map(p -> UserCouponCommand.PublishFinish.of(p.getCouponId(), p.getQuantity()))
            .filter(userCouponService::isPublishFinished)
            .forEach(p -> couponService.finishCoupon(p.getCouponId()));
    }

    @Transactional(readOnly = true)
    public UserCouponResult.Coupons getUserCoupons(Long userId) {
        userService.getUser(userId);

        List<UserCouponResult.Coupon> coupons = userCouponService.getUserCoupons(userId).getCoupons().stream()
            .map(this::getUserCoupon)
            .toList();
        return UserCouponResult.Coupons.of(coupons);
    }

    private UserCouponResult.Coupon getUserCoupon(UserCouponInfo.Coupon userCoupon) {
        CouponInfo.Coupon coupon = couponService.getCoupon(userCoupon.getCouponId());

        return UserCouponResult.Coupon.builder()
            .userCouponId(userCoupon.getUserCouponId())
            .couponName(coupon.getName())
            .discountRate(coupon.getDiscountRate())
            .build();
    }
}
