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
    public static class PaidProduct {

        private final Long productId;
        private final int quantity;

        public PaidProduct(Long productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public static PaidProduct of(Long productId, int quantity) {
            return new PaidProduct(productId, quantity);
        }
    }

    @Getter
    public static class PaidProducts {

        private final List<PaidProduct> products;

        private PaidProducts(List<PaidProduct> products) {
            this.products = products;
        }

        public static PaidProducts of(List<PaidProduct> products) {
            return new PaidProducts(products);
        }
    }
}
