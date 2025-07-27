package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.balance.BalanceCommand;
import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.payment.PaymentCommand;
import kr.hhplus.be.server.domain.product.ProductCommand;
import kr.hhplus.be.server.domain.product.ProductInfo;
import kr.hhplus.be.server.domain.stock.StockCommand;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderCriteria {

    @Getter
    public static class OrderPayment {

        private final Long userId;
        private final List<OrderProduct> products;

        private OrderPayment(Long userId, List<OrderProduct> products) {
            this.userId = userId;
            this.products = products;
        }

        public static OrderPayment of(Long userId,  List<OrderProduct> products) {
            return new OrderPayment(userId, products);
        }

        public ProductCommand.OrderProducts toProductCommand() {
            return ProductCommand.OrderProducts.of(
                products.stream()
                    .map(o -> ProductCommand.OrderProduct.of(o.getProductId(), o.getQuantity()))
                    .toList()
            );
        }

        public OrderCommand.Create toOrderCommand(ProductInfo.OrderProducts productInfo) {
            List<OrderCommand.OrderProduct> orderProducts = productInfo.getProducts().stream()
                .map(p -> OrderCommand.OrderProduct.builder()
                    .productId(p.getProductId())
                    .productName(p.getProductName())
                    .productPrice(p.getProductPrice())
                    .quantity(p.getQuantity())
                    .build()
                ).toList();

            return OrderCommand.Create.of(userId, orderProducts);
        }

        public BalanceCommand.Use toBalanceCommand(long totalPrice) {
            return BalanceCommand.Use.of(userId, totalPrice);
        }

        public StockCommand.OrderProducts toStockCommand() {
            return StockCommand.OrderProducts.of(
                products.stream()
                    .map(o -> StockCommand.OrderProduct.of(o.getProductId(), o.getQuantity()))
                    .toList()
            );
        }

        public PaymentCommand.Payment toPaymentCommand(OrderInfo.Order order) {
            return PaymentCommand.Payment.of(order.getOrderId(), userId, order.getTotalPrice());
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
}
