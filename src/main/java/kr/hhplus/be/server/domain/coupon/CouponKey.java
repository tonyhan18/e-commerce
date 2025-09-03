package kr.hhplus.be.server.domain.coupon;

import java.util.List;

import kr.hhplus.be.server.support.key.KeyGeneratable;
import kr.hhplus.be.server.support.key.KeyType;

public class CouponKey implements KeyGeneratable{

    private final Long couponId;

    private CouponKey(Long couponId) {
        this.couponId = couponId;
    }

    public static CouponKey of(Long couponId) {
        return new CouponKey(couponId);
    }

    @Override
    public KeyType type() {
        return KeyType.USER_COUPON;
    }

    @Override
    public List<String> namespaces() {
        return List.of(couponId.toString());
    }
}
