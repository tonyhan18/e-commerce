package kr.hhplus.be.server.interfaces.products;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import kr.hhplus.be.server.application.product.ProductResult;
import lombok.Builder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductResponse {
    @Getter
    @NoArgsConstructor
    public static class Products {

        private List<Product> products;

        private Products(List<Product> products) {
            this.products = products;
        }

        public static Products of(ProductResult.Products products) {
            return new Products(products.getProducts().stream()
                .map(Product::of)
                .toList());
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Product {

        private Long id;
        private String name;
        private long price;
        private int stock;

        @Builder
        private Product(Long id, String name, long price, int stock) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.stock = stock;
        }

        public static Product of(ProductResult.Product product) {
            return Product.builder()
                .id(product.getProductId())
                .name(product.getProductName())
                .price(product.getProductPrice())
                .stock(product.getQuantity())
                .build();
        }
    }
} 