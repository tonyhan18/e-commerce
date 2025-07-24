package kr.hhplus.be.server.domain.coupon;

import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository {

    Coupon findById(Long couponId);
}
