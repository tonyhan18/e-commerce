package kr.hhplus.be.server.domain.product;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class ProductCommand {
    
    @Getter
    public static class OrderProducts {

        private final List<OrderProduct> products;

        private OrderProducts(List<OrderProduct> products) {
            this.products = products;
        }

        public static OrderProducts of(List<OrderProduct> products) {
            return new OrderProducts(products);
        }
    }

    @Getter
    public static class OrderProduct {

        private final Long productId;
        private final int quantity;

        private OrderProduct(Long productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public static OrderProduct of(Long productId, int quantity) {
            return new OrderProduct(productId, quantity);
        }
    }

    @Getter
    public static class Products {

        private final List<Long> productIds;

        private Products(List<Long> productIds) {
            this.productIds = productIds;
        }

        public static Products of(List<Long> productIds) {
            return new Products(productIds);
        }
    }
}
