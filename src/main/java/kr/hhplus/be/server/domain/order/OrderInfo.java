package kr.hhplus.be.server.domain.order;

import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Getter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderInfo {
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

        public static Order of(Long orderId, Long totalPrice, Long discountPrice) {
            return new Order(orderId, totalPrice, discountPrice);
        }
    }

    @Getter
    public static class TopPaidProducts {
        private final List<Long> productIds;

        private TopPaidProducts(List<Long> productIds) {
            this.productIds = productIds;
        }

        public static TopPaidProducts of(List<Long> productIds) {
            return new TopPaidProducts(productIds);
        }
    }
}
