package kr.hhplus.be.server.domain.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderProductTest {

    @Test
    @DisplayName("정적 팩토리 메서드로 주문상품을 생성할 수 있다.")
    void createOrderProduct() {
        // given
        Long productId = 1L;
        String productName = "테스트상품";
        long unitPrice = 10000L;
        int quantity = 3;

        // when
        OrderProduct orderProduct = OrderProduct.create(productId, productName, unitPrice, quantity);

        // then
        assertThat(orderProduct.getProductId()).isEqualTo(productId);
        assertThat(orderProduct.getProductName()).isEqualTo(productName);
        assertThat(orderProduct.getUnitPrice()).isEqualTo(unitPrice);
        assertThat(orderProduct.getQuantity()).isEqualTo(quantity);
        assertThat(orderProduct.getOrder()).isNull();
        assertThat(orderProduct.getId()).isNull();
    }

    @Test
    @DisplayName("총 가격이 올바르게 계산된다.")
    void getPrice() {
        // given
        OrderProduct orderProduct = OrderProduct.create(1L, "상품", 5000L, 4);

        // when
        long price = orderProduct.getPrice();

        // then
        assertThat(price).isEqualTo(20000L);
    }

    @Test
    @DisplayName("빌더로 주문상품을 생성할 수 있다.")
    void builderCreateOrderProduct() {
        // given
        Long productId = 2L;
        String productName = "빌더상품";
        long unitPrice = 7000L;
        int quantity = 2;

        // when
        OrderProduct orderProduct = OrderProduct.builder()
                .productId(productId)
                .productName(productName)
                .unitPrice(unitPrice)
                .quantity(quantity)
                .build();

        // then
        assertThat(orderProduct.getProductId()).isEqualTo(productId);
        assertThat(orderProduct.getProductName()).isEqualTo(productName);
        assertThat(orderProduct.getUnitPrice()).isEqualTo(unitPrice);
        assertThat(orderProduct.getQuantity()).isEqualTo(quantity);
    }

    @Test
    @DisplayName("Order를 연결할 수 있다.")
    void setOrder() {
        // given
        OrderProduct orderProduct = OrderProduct.create(1L, "상품", 1000L, 1);
        Order order = new Order();

        // when
        orderProduct.setOrder(order);

        // then
        assertThat(orderProduct.getOrder()).isEqualTo(order);
    }
}
