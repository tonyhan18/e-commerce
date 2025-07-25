package kr.hhplus.be.server.interfaces.orders;

import kr.hhplus.be.server.application.order.OrderCriteria;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderRequest {

    @Getter
    @NoArgsConstructor
    public static class OrderPayment {

        @NotNull(message = "사용자 ID는 필수 입니다.")
        private Long userId;

        private Long userCouponId;

        @Valid
        @NotEmpty(message = "상품 목록은 1개 이상이여야 합니다.")
        private List<OrderProduct> products;

        private OrderPayment(Long userId, List<OrderProduct> products, Long userCouponId) {
            this.userId = userId;
            this.products = products;
            this.userCouponId = userCouponId;
        }

        public static OrderPayment of(Long userId, List<OrderProduct> products, Long userCouponId) {
            return new OrderPayment(userId, products, userCouponId);
        }

        public OrderCriteria.OrderPayment toCriteria() {
            return OrderCriteria.OrderPayment.of(userId, products.stream()
                    .map(OrderProduct::toCriteria)
                    .toList(), userCouponId);
        }
    }

    @Getter
    @NoArgsConstructor
    public static class OrderProduct {

        @NotNull(message = "상품 ID는 필수입니다.")
        private Long id;

        @NotNull(message = "상품 구매 수량은 필수입니다.")
        @Positive(message = "상품 구매 수량은 양수여야 합니다.")
        private Integer quantity;

        private OrderProduct(Long id, Integer quantity) {
            this.id = id;
            this.quantity = quantity;
        }

        public static OrderProduct of(Long id, Integer quantity) {
            return new OrderProduct(id, quantity);
        }

        public OrderCriteria.OrderProduct toCriteria() {
            return OrderCriteria.OrderProduct.of(id, quantity);
        }
    }

}
