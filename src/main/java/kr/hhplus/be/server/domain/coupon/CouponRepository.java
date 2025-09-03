package kr.hhplus.be.server.domain.coupon;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository {

    Coupon save(Coupon coupon);

    Coupon findCouponById(Long couponId);

    List<Coupon> findByStatus(CouponStatus status);

    UserCoupon save(UserCoupon userCoupon);

    UserCoupon findByUserIdAndCouponId(Long userId, Long couponId);

    UserCoupon findUserCouponById(Long userCouponId);

    List<CouponInfo.Coupon> findByUserId(Long userId);

    boolean save(CouponCommand.PublishRequest command);

    int countByCouponId(Long couponId);

    List<CouponInfo.Candidates> findPublishCandidates(CouponCommand.Candidates command);

    void saveAll(List<UserCoupon> userCoupons);
}
