package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {

    private final CouponJpaRepository couponJpaRepository;

    @Override
    public Coupon save(Coupon coupon) {
        return couponJpaRepository.save(coupon);
    }

    @Override
    public Coupon findById(Long couponId) {
        return couponJpaRepository.findById(couponId)
            .orElseThrow(() -> new IllegalArgumentException("쿠폰이 존재하지 않습니다."));
    }

    @Override
    public Coupon findWithLockById(Long couponId) {
        return couponJpaRepository.findWithLockById(couponId)
            .orElseThrow(() -> new IllegalArgumentException("쿠폰이 존재하지 않습니다."));
    }
}
