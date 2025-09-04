package kr.hhplus.be.server.infrastructure.payment.client;

import kr.hhplus.be.server.domain.payment.PaymentClient;
import kr.hhplus.be.server.domain.payment.PaymentInfo;
import org.springframework.stereotype.Component;

@Component
public class PaymentApiClient implements PaymentClient {

    @Override
    public void useBalance(Long userId, long amount) {

    }

    @Override
    public void useCoupon(Long userId, Long userCouponId) {

    }

    @Override
    public PaymentInfo.Order getOrder(Long orderId) {
        return null;
    }

    @Override
    public void refundBalance(Long userId, long amount) {

    }

    @Override
    public void cancelCoupon(Long userCouponId) {

    }
}
