package kr.hhplus.be.server.domain.coupon;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository {

    Coupon save(Coupon coupon);

    Coupon findCouponById(Long couponId);

    List<Coupon> findByStatus(CouponStatus status);

    UserCoupon save(UserCoupon userCoupon);

    Optional<UserCoupon> findByUserIdAndCouponId(Long userId, Long couponId);

    UserCoupon findUserCouponById(Long userCouponId);

    List<CouponInfo.Coupon> findByUserId(Long userId);

    boolean findPublishableCouponById(Long couponId);

    void updateAvailableCoupon(Long couponId, boolean status);
}
