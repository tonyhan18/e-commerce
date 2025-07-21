package kr.hhplus.be.server.domain.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderProductTest {

    @Test
    @DisplayName("주문 상품을 생성할 수 있다.")
    void createOrderProduct() {
        // given
        Long productId = 1L;
        String productName = "테스트 상품";
        long unitPrice = 10000L;
        int quantity = 2;

        // when
        OrderProduct orderProduct = OrderProduct.create(productId, productName, unitPrice, quantity);

        // then
        assertThat(orderProduct.getProductId()).isEqualTo(productId);
        assertThat(orderProduct.getProductName()).isEqualTo(productName);
        assertThat(orderProduct.getUnitPrice()).isEqualTo(unitPrice);
        assertThat(orderProduct.getQuantity()).isEqualTo(quantity);
        assertThat(orderProduct.getId()).isNull(); // ID는 저장 시점에 생성됨
    }

    @Test
    @DisplayName("주문 상품의 총 가격을 계산할 수 있다.")
    void getPrice() {
        // given
        Long productId = 1L;
        String productName = "테스트 상품";
        long unitPrice = 10000L;
        int quantity = 3;

        OrderProduct orderProduct = OrderProduct.create(productId, productName, unitPrice, quantity);

        // when
        long totalPrice = orderProduct.getPrice();

        // then
        assertThat(totalPrice).isEqualTo(30000L); // 10000 * 3
    }

    @Test
    @DisplayName("수량이 1일 때 총 가격이 단가와 같다.")
    void getPriceWithQuantityOne() {
        // given
        Long productId = 1L;
        String productName = "테스트 상품";
        long unitPrice = 15000L;
        int quantity = 1;

        OrderProduct orderProduct = OrderProduct.create(productId, productName, unitPrice, quantity);

        // when
        long totalPrice = orderProduct.getPrice();

        // then
        assertThat(totalPrice).isEqualTo(15000L); // 15000 * 1
    }

    @Test
    @DisplayName("수량이 0일 때 총 가격이 0이다.")
    void getPriceWithQuantityZero() {
        // given
        Long productId = 1L;
        String productName = "테스트 상품";
        long unitPrice = 10000L;
        int quantity = 0;

        OrderProduct orderProduct = OrderProduct.create(productId, productName, unitPrice, quantity);

        // when
        long totalPrice = orderProduct.getPrice();

        // then
        assertThat(totalPrice).isEqualTo(0L); // 10000 * 0
    }

    @Test
    @DisplayName("단가가 0일 때 총 가격이 0이다.")
    void getPriceWithZeroUnitPrice() {
        // given
        Long productId = 1L;
        String productName = "무료 상품";
        long unitPrice = 0L;
        int quantity = 5;

        OrderProduct orderProduct = OrderProduct.create(productId, productName, unitPrice, quantity);

        // when
        long totalPrice = orderProduct.getPrice();

        // then
        assertThat(totalPrice).isEqualTo(0L); // 0 * 5
    }

    @Test
    @DisplayName("주문을 설정할 수 있다.")
    void setOrder() {
        // given
        Long productId = 1L;
        String productName = "테스트 상품";
        long unitPrice = 10000L;
        int quantity = 2;

        OrderProduct orderProduct = OrderProduct.create(productId, productName, unitPrice, quantity);
        
        Order order = Order.create(1L, 1L, List.of(orderProduct), 0.1);

        // when
        orderProduct.setOrder(order);

        // then
        assertThat(orderProduct.getOrder()).isEqualTo(order);
    }

    @Test
    @DisplayName("주문을 null로 설정할 수 있다.")
    void setOrderToNull() {
        // given
        Long productId = 1L;
        String productName = "테스트 상품";
        long unitPrice = 10000L;
        int quantity = 2;

        OrderProduct orderProduct = OrderProduct.create(productId, productName, unitPrice, quantity);

        // when
        orderProduct.setOrder(null);

        // then
        assertThat(orderProduct.getOrder()).isNull();
    }

    @Test
    @DisplayName("대량 수량으로 주문 상품을 생성할 수 있다.")
    void createOrderProductWithHighQuantity() {
        // given
        Long productId = 1L;
        String productName = "대량 상품";
        long unitPrice = 1000L;
        int quantity = 1000;

        // when
        OrderProduct orderProduct = OrderProduct.create(productId, productName, unitPrice, quantity);

        // then
        assertThat(orderProduct.getProductId()).isEqualTo(productId);
        assertThat(orderProduct.getProductName()).isEqualTo(productName);
        assertThat(orderProduct.getUnitPrice()).isEqualTo(unitPrice);
        assertThat(orderProduct.getQuantity()).isEqualTo(quantity);
        assertThat(orderProduct.getPrice()).isEqualTo(1000000L); // 1000 * 1000
    }

    @Test
    @DisplayName("높은 단가로 주문 상품을 생성할 수 있다.")
    void createOrderProductWithHighUnitPrice() {
        // given
        Long productId = 1L;
        String productName = "고가 상품";
        long unitPrice = 1000000L;
        int quantity = 1;

        // when
        OrderProduct orderProduct = OrderProduct.create(productId, productName, unitPrice, quantity);

        // then
        assertThat(orderProduct.getProductId()).isEqualTo(productId);
        assertThat(orderProduct.getProductName()).isEqualTo(productName);
        assertThat(orderProduct.getUnitPrice()).isEqualTo(unitPrice);
        assertThat(orderProduct.getQuantity()).isEqualTo(quantity);
        assertThat(orderProduct.getPrice()).isEqualTo(1000000L); // 1000000 * 1
    }

    @Test
    @DisplayName("여러 주문 상품의 가격을 비교할 수 있다.")
    void compareOrderProductPrices() {
        // given
        OrderProduct orderProduct1 = OrderProduct.create(1L, "상품1", 10000L, 2);
        OrderProduct orderProduct2 = OrderProduct.create(2L, "상품2", 5000L, 4);
        OrderProduct orderProduct3 = OrderProduct.create(3L, "상품3", 20000L, 1);

        // when & then
        assertThat(orderProduct1.getPrice()).isEqualTo(20000L); // 10000 * 2
        assertThat(orderProduct2.getPrice()).isEqualTo(20000L); // 5000 * 4
        assertThat(orderProduct3.getPrice()).isEqualTo(20000L); // 20000 * 1
        
        // 세 상품의 총 가격이 모두 같음
        assertThat(orderProduct1.getPrice()).isEqualTo(orderProduct2.getPrice());
        assertThat(orderProduct2.getPrice()).isEqualTo(orderProduct3.getPrice());
    }

    @Test
    @DisplayName("주문 상품 생성 후 주문 관계가 null인지 확인한다.")
    void checkOrderIsNullAfterCreation() {
        // given
        Long productId = 1L;
        String productName = "테스트 상품";
        long unitPrice = 10000L;
        int quantity = 1;

        // when
        OrderProduct orderProduct = OrderProduct.create(productId, productName, unitPrice, quantity);

        // then
        assertThat(orderProduct.getOrder()).isNull();
    }

    @Test
    @DisplayName("주문 상품의 모든 필드가 올바르게 설정되는지 확인한다.")
    void checkAllFieldsAreSetCorrectly() {
        // given
        Long productId = 999L;
        String productName = "복잡한 상품명 with 특수문자!@#";
        long unitPrice = 12345L;
        int quantity = 7;

        // when
        OrderProduct orderProduct = OrderProduct.create(productId, productName, unitPrice, quantity);

        // then
        assertThat(orderProduct.getProductId()).isEqualTo(999L);
        assertThat(orderProduct.getProductName()).isEqualTo("복잡한 상품명 with 특수문자!@#");
        assertThat(orderProduct.getUnitPrice()).isEqualTo(12345L);
        assertThat(orderProduct.getQuantity()).isEqualTo(7);
        assertThat(orderProduct.getPrice()).isEqualTo(86415L); // 12345 * 7
    }

    @Test
    @DisplayName("주문 상품의 가격 계산이 정확한지 확인한다.")
    void verifyPriceCalculationAccuracy() {
        // given
        OrderProduct orderProduct1 = OrderProduct.create(1L, "상품1", 1000L, 10);
        OrderProduct orderProduct2 = OrderProduct.create(2L, "상품2", 2500L, 4);
        OrderProduct orderProduct3 = OrderProduct.create(3L, "상품3", 500L, 20);

        // when & then
        assertThat(orderProduct1.getPrice()).isEqualTo(10000L); // 1000 * 10
        assertThat(orderProduct2.getPrice()).isEqualTo(10000L); // 2500 * 4
        assertThat(orderProduct3.getPrice()).isEqualTo(10000L); // 500 * 20
        
        // 모든 상품의 총 가격이 10000원으로 동일
        assertThat(orderProduct1.getPrice()).isEqualTo(orderProduct2.getPrice());
        assertThat(orderProduct2.getPrice()).isEqualTo(orderProduct3.getPrice());
    }

    @Test
    @DisplayName("주문 상품의 ID가 생성 시점에 null인지 확인한다.")
    void checkIdIsNullOnCreation() {
        // given
        Long productId = 1L;
        String productName = "테스트 상품";
        long unitPrice = 10000L;
        int quantity = 1;

        // when
        OrderProduct orderProduct = OrderProduct.create(productId, productName, unitPrice, quantity);

        // then
        assertThat(orderProduct.getId()).isNull(); // ID는 저장 시점에 생성됨
    }
} 