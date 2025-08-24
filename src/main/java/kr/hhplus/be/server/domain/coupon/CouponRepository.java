package kr.hhplus.be.server.domain.coupon;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository {

    Coupon findById(Long couponId);
    Coupon save(Coupon coupon);
    Coupon findByIdWithLock(Long couponId);
    List<Coupon> findByStatus(CouponStatus status);
}
