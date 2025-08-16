package kr.hhplus.be.server.domain.coupon;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    @Transactional
    public void publishCoupon(Long couponId) {
        Coupon coupon = couponRepository.findByIdWithLock(couponId);
        coupon.publish();
        couponRepository.save(coupon); // 변경사항을 데이터베이스에 저장
    }

    public CouponInfo.Coupon getCoupon(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId);
        return CouponInfo.Coupon.builder()
                .couponId(coupon.getId())
                .name(coupon.getName())
                .discountRate(coupon.getDiscountRate())
                .build();
    }
}
