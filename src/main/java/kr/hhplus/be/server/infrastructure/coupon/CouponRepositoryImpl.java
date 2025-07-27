package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;

import org.springframework.stereotype.Repository;

@Repository
public class CouponRepositoryImpl implements CouponRepository {

    @Override
    public Coupon findById(Long couponId) {
        return null;
    }
}
