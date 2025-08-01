package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.OrderInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderResult {
    
    @Getter
    public static class Order {
        private final Long orderId;
        private final Long totalPrice;
        private final Long discountPrice;

        private Order(Long orderId, Long totalPrice, Long discountPrice) {
            this.orderId = orderId;
            this.totalPrice = totalPrice;
            this.discountPrice = discountPrice;
        }

        public static Order of(OrderInfo.Order order) {
            return new Order(order.getOrderId(), order.getTotalPrice(), order.getDiscountPrice());
        }        
    }
}
