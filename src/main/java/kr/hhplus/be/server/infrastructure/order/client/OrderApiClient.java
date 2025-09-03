package kr.hhplus.be.server.infrastructure.order.client;

import kr.hhplus.be.server.domain.order.OrderClient;
import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.order.OrderInfo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderApiClient implements OrderClient {

    @Override
    public OrderInfo.User getUser(Long userId) {
        return null;
    }

    @Override
    public List<OrderInfo.Product> getProducts(List<OrderCommand.OrderProduct> command) {
        return List.of();
    }

    @Override
    public OrderInfo.Coupon getUsableCoupon(Long userCouponId) {
        return null;
    }
}
