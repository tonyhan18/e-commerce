package kr.hhplus.be.server.infrastructure.coupon.client;

import kr.hhplus.be.server.domain.coupon.CouponClient;
import kr.hhplus.be.server.domain.coupon.CouponInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponApiClient implements CouponClient {

    @Override
    public CouponInfo.User getUser(Long userId) {
        return null;
    }
}
