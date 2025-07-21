package kr.hhplus.be.server.domain.product;

import java.util.List;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class ProductInfo {
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
            return new OrderProduct(productId, productName, productPrice, quantity);
        }
    }

    @Getter
    public static class Products {

        private final List<Product> products;

        private Products(List<Product> products) {
            this.products = products;
        }

        public static Products of(List<Product> products) {
            return new Products(products);
        }
    }

    @Getter
    public static class Product {

        private final Long productId;
        private final String productName;
        private final Long productPrice;

        @Builder
        private Product(Long productId, String productName, Long productPrice) {
            this.productId = productId;
            this.productName = productName;
            this.productPrice = productPrice;
        }
    }
}
