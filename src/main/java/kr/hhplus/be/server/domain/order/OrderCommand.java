package kr.hhplus.be.server.domain.order;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderCommand {

    @Getter
    public static class Create {

        private final Long userId;
        private final List<OrderProduct> products;
        private final Double discountRate;
        private final Long UserCouponId;

        @Builder
        private Create(Long userId, List<OrderProduct> products, Long userCouponId, Double discountRate) {
            this.userId = userId;
            this.products = products;
            this.UserCouponId = userCouponId;
            this.discountRate = discountRate;
        }

        public static Create of(Long userId, List<OrderProduct> products, Long userCouponId, Double discountRate) {
            return Create.builder()
                .userId(userId)
                .products(products)
                .userCouponId(userCouponId)
                .discountRate(discountRate)
                .build();
        }
    }

    @Getter
    public static class OrderProduct {

        private final Long productId;
        private final String productName;
        private final Long productPrice;
        private final int quantity;

        @Builder
        private OrderProduct(Long productId, String productName, Long productPrice, int quantity) {
            this.productId = productId;
            this.productName = productName;
            this.productPrice = productPrice;
            this.quantity = quantity;
        }

        public static OrderProduct of(Long productId, String productName, Long productPrice, int quantity) {
            return OrderProduct.builder()
                .productId(productId)
                .productName(productName)
                .productPrice(productPrice)
                .quantity(quantity)
                .build();
        }
    }

    @Getter
    public static class TopOrders {

        private final List<Long> orderIds;
        private final int limit;

        private TopOrders(List<Long> orderIds, int limit) {
            this.orderIds = orderIds;
            this.limit = limit;
        }

        public static TopOrders of(List<Long> orderIds, int limit) {
            return new TopOrders(orderIds, limit);
        }
    }
}
