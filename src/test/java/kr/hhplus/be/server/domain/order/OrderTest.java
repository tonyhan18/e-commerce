package kr.hhplus.be.server.domain.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    @Test
    @DisplayName("주문을 생성할 수 있다.")
    void createOrder() {
        // given
        Long userId = 1L;
        List<OrderProduct> orderProducts = Arrays.asList(
            OrderProduct.create(1L, "상품1", 10000L, 2),
            OrderProduct.create(2L, "상품2", 5000L, 1)
        );

        // when
        Order order = Order.create(userId, orderProducts);

        // then
        assertThat(order.getUserId()).isEqualTo(userId);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(order.getTotalPrice()).isEqualTo(25000L); // (10000 * 2) + (5000 * 1)
        assertThat(order.getOrderProducts()).hasSize(2);
        assertThat(order.getUserCouponId()).isNull();
        assertThat(order.getDiscountPrice()).isEqualTo(0L);
    }

    @Test
    @DisplayName("주문 상품이 null이면 예외가 발생한다.")
    void createOrderWithNullOrderProducts() {
        // given
        Long userId = 1L;
        List<OrderProduct> orderProducts = null;

        // when & then
        assertThatThrownBy(() -> Order.create(userId, orderProducts))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("주문 상품이 없습니다.");
    }

    @Test
    @DisplayName("주문 상품이 비어있으면 예외가 발생한다.")
    void createOrderWithEmptyOrderProducts() {
        // given
        Long userId = 1L;
        List<OrderProduct> orderProducts = new ArrayList<>();

        // when & then
        assertThatThrownBy(() -> Order.create(userId, orderProducts))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("주문 상품이 없습니다.");
    }

    @Test
    @DisplayName("단일 상품으로 주문을 생성할 수 있다.")
    void createOrderWithSingleProduct() {
        // given
        Long userId = 1L;
        List<OrderProduct> orderProducts = Arrays.asList(
            OrderProduct.create(1L, "단일 상품", 15000L, 1)
        );

        // when
        Order order = Order.create(userId, orderProducts);

        // then
        assertThat(order.getUserId()).isEqualTo(userId);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(order.getTotalPrice()).isEqualTo(15000L);
        assertThat(order.getOrderProducts()).hasSize(1);
    }

    @Test
    @DisplayName("여러 상품으로 주문을 생성할 수 있다.")
    void createOrderWithMultipleProducts() {
        // given
        Long userId = 1L;
        List<OrderProduct> orderProducts = Arrays.asList(
            OrderProduct.create(1L, "상품1", 10000L, 3),
            OrderProduct.create(2L, "상품2", 5000L, 2),
            OrderProduct.create(3L, "상품3", 20000L, 1)
        );

        // when
        Order order = Order.create(userId, orderProducts);

        // then
        assertThat(order.getUserId()).isEqualTo(userId);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(order.getTotalPrice()).isEqualTo(60000L); // (10000 * 3) + (5000 * 2) + (20000 * 1)
        assertThat(order.getOrderProducts()).hasSize(3);
    }

    @Test
    @DisplayName("주문 상태를 결제 완료로 변경할 수 있다.")
    void paid() {
        // given
        Long userId = 1L;
        List<OrderProduct> orderProducts = Arrays.asList(
            OrderProduct.create(1L, "상품1", 10000L, 1)
        );
        Order order = Order.create(userId, orderProducts);

        // when
        order.paid();

        // then
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    @DisplayName("주문 상품의 가격이 올바르게 계산된다.")
    void calculateTotalPrice() {
        // given
        Long userId = 1L;
        List<OrderProduct> orderProducts = Arrays.asList(
            OrderProduct.create(1L, "상품1", 10000L, 2), // 20000
            OrderProduct.create(2L, "상품2", 5000L, 3),  // 15000
            OrderProduct.create(3L, "상품3", 25000L, 1)  // 25000
        );

        // when
        Order order = Order.create(userId, orderProducts);

        // then
        assertThat(order.getTotalPrice()).isEqualTo(60000L);
    }

    @Test
    @DisplayName("주문 상품과 주문이 양방향으로 연결된다.")
    void orderProductBidirectionalRelationship() {
        // given
        Long userId = 1L;
        List<OrderProduct> orderProducts = Arrays.asList(
            OrderProduct.create(1L, "상품1", 10000L, 1)
        );

        // when
        Order order = Order.create(userId, orderProducts);

        // then
        assertThat(order.getOrderProducts()).hasSize(1);
        assertThat(order.getOrderProducts().get(0).getOrder()).isEqualTo(order);
    }

    @Test
    @DisplayName("주문 생성 시 할인 가격은 0으로 초기화된다.")
    void discountPriceInitializedToZero() {
        // given
        Long userId = 1L;
        List<OrderProduct> orderProducts = Arrays.asList(
            OrderProduct.create(1L, "상품1", 10000L, 1)
        );

        // when
        Order order = Order.create(userId, orderProducts);

        // then
        assertThat(order.getDiscountPrice()).isEqualTo(0L);
    }

    @Test
    @DisplayName("주문 생성 시 쿠폰 ID는 null로 초기화된다.")
    void userCouponIdInitializedToNull() {
        // given
        Long userId = 1L;
        List<OrderProduct> orderProducts = Arrays.asList(
            OrderProduct.create(1L, "상품1", 10000L, 1)
        );

        // when
        Order order = Order.create(userId, orderProducts);

        // then
        assertThat(order.getUserCouponId()).isNull();
    }

    @Test
    @DisplayName("주문 ID는 생성 시점에 null이다.")
    void orderIdIsNullOnCreation() {
        // given
        Long userId = 1L;
        List<OrderProduct> orderProducts = Arrays.asList(
            OrderProduct.create(1L, "상품1", 10000L, 1)
        );

        // when
        Order order = Order.create(userId, orderProducts);

        // then
        assertThat(order.getId()).isNull();
    }
} 