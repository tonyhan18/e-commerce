package kr.hhplus.be.server.domain.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    @Test
    @DisplayName("주문을 생성할 수 있다.")
    void createOrder() {
        // given
        Long userId = 1L;
        Long userCouponId = 1L;
        double discountRate = 0.1; // 10% 할인

        OrderProduct orderProduct1 = OrderProduct.create(1L, "상품1", 10000L, 2);
        OrderProduct orderProduct2 = OrderProduct.create(2L, "상품2", 5000L, 1);
        List<OrderProduct> orderProducts = List.of(orderProduct1, orderProduct2);

        // when
        Order order = Order.create(userId, userCouponId, orderProducts, discountRate);

        // then
        assertThat(order.getUserId()).isEqualTo(userId);
        assertThat(order.getUserCouponId()).isEqualTo(userCouponId);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CREATED);
        
        // 총 가격: (10000 * 2) + (5000 * 1) = 25000
        assertThat(order.getTotalPrice()).isEqualTo(25000L);
        
        // 할인 가격: 25000 * 0.1 = 2500
        assertThat(order.getDiscountPrice()).isEqualTo(2500L);
    }

    @Test
    @DisplayName("주문 상품이 없으면 예외가 발생한다.")
    void createOrderWithEmptyProducts() {
        // given
        Long userId = 1L;
        Long userCouponId = 1L;
        double discountRate = 0.1;
        List<OrderProduct> orderProducts = List.of();

        // when & then
        assertThatThrownBy(() -> Order.create(userId, userCouponId, orderProducts, discountRate))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("주문 상품이 없습니다.");
    }

    @Test
    @DisplayName("주문 상품이 null이면 예외가 발생한다.")
    void createOrderWithNullProducts() {
        // given
        Long userId = 1L;
        Long userCouponId = 1L;
        double discountRate = 0.1;
        List<OrderProduct> orderProducts = null;

        // when & then
        assertThatThrownBy(() -> Order.create(userId, userCouponId, orderProducts, discountRate))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("주문 상품이 없습니다.");
    }

    @Test
    @DisplayName("단일 상품으로 주문을 생성할 수 있다.")
    void createOrderWithSingleProduct() {
        // given
        Long userId = 1L;
        Long userCouponId = 1L;
        double discountRate = 0.2; // 20% 할인

        OrderProduct orderProduct = OrderProduct.create(1L, "단일 상품", 15000L, 1);
        List<OrderProduct> orderProducts = List.of(orderProduct);

        // when
        Order order = Order.create(userId, userCouponId, orderProducts, discountRate);

        // then
        assertThat(order.getUserId()).isEqualTo(userId);
        assertThat(order.getUserCouponId()).isEqualTo(userCouponId);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(order.getTotalPrice()).isEqualTo(15000L);
        assertThat(order.getDiscountPrice()).isEqualTo(3000L); // 15000 * 0.2
    }

    @Test
    @DisplayName("할인율이 0일 때 주문을 생성할 수 있다.")
    void createOrderWithZeroDiscountRate() {
        // given
        Long userId = 1L;
        Long userCouponId = 1L;
        double discountRate = 0.0; // 0% 할인

        OrderProduct orderProduct = OrderProduct.create(1L, "상품", 10000L, 1);
        List<OrderProduct> orderProducts = List.of(orderProduct);

        // when
        Order order = Order.create(userId, userCouponId, orderProducts, discountRate);

        // then
        assertThat(order.getTotalPrice()).isEqualTo(10000L);
        assertThat(order.getDiscountPrice()).isEqualTo(0L); // 10000 * 0.0
    }

    @Test
    @DisplayName("할인율이 1일 때 주문을 생성할 수 있다.")
    void createOrderWithFullDiscountRate() {
        // given
        Long userId = 1L;
        Long userCouponId = 1L;
        double discountRate = 1.0; // 100% 할인

        OrderProduct orderProduct = OrderProduct.create(1L, "상품", 10000L, 1);
        List<OrderProduct> orderProducts = List.of(orderProduct);

        // when
        Order order = Order.create(userId, userCouponId, orderProducts, discountRate);

        // then
        assertThat(order.getTotalPrice()).isEqualTo(10000L);
        assertThat(order.getDiscountPrice()).isEqualTo(10000L); // 10000 * 1.0
    }

    @Test
    @DisplayName("여러 상품으로 주문을 생성할 수 있다.")
    void createOrderWithMultipleProducts() {
        // given
        Long userId = 1L;
        Long userCouponId = 1L;
        double discountRate = 0.15; // 15% 할인

        OrderProduct orderProduct1 = OrderProduct.create(1L, "상품1", 10000L, 3);
        OrderProduct orderProduct2 = OrderProduct.create(2L, "상품2", 5000L, 2);
        OrderProduct orderProduct3 = OrderProduct.create(3L, "상품3", 2000L, 5);
        List<OrderProduct> orderProducts = List.of(orderProduct1, orderProduct2, orderProduct3);

        // when
        Order order = Order.create(userId, userCouponId, orderProducts, discountRate);

        // then
        // 총 가격: (10000 * 3) + (5000 * 2) + (2000 * 5) = 30000 + 10000 + 10000 = 50000
        assertThat(order.getTotalPrice()).isEqualTo(50000L);
        
        // 할인 가격: 50000 * 0.15 = 7500
        assertThat(order.getDiscountPrice()).isEqualTo(7500L);
    }

    @Test
    @DisplayName("주문을 결제할 수 있다.")
    void payOrder() {
        // given
        Long userId = 1L;
        Long userCouponId = 1L;
        double discountRate = 0.1;

        OrderProduct orderProduct = OrderProduct.create(1L, "상품", 10000L, 1);
        List<OrderProduct> orderProducts = List.of(orderProduct);

        Order order = Order.create(userId, userCouponId, orderProducts, discountRate);

        // when
        order.paid();

        // then
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    @DisplayName("쿠폰이 없는 주문을 생성할 수 있다.")
    void createOrderWithoutCoupon() {
        // given
        Long userId = 1L;
        Long userCouponId = null;
        double discountRate = 0.0;

        OrderProduct orderProduct = OrderProduct.create(1L, "상품", 10000L, 1);
        List<OrderProduct> orderProducts = List.of(orderProduct);

        // when
        Order order = Order.create(userId, userCouponId, orderProducts, discountRate);

        // then
        assertThat(order.getUserId()).isEqualTo(userId);
        assertThat(order.getUserCouponId()).isNull();
        assertThat(order.getTotalPrice()).isEqualTo(10000L);
        assertThat(order.getDiscountPrice()).isEqualTo(0L);
    }

    @Test
    @DisplayName("수량이 많은 상품으로 주문을 생성할 수 있다.")
    void createOrderWithHighQuantity() {
        // given
        Long userId = 1L;
        Long userCouponId = 1L;
        double discountRate = 0.05; // 5% 할인

        OrderProduct orderProduct = OrderProduct.create(1L, "대량 상품", 1000L, 100);
        List<OrderProduct> orderProducts = List.of(orderProduct);

        // when
        Order order = Order.create(userId, userCouponId, orderProducts, discountRate);

        // then
        // 총 가격: 1000 * 100 = 100000
        assertThat(order.getTotalPrice()).isEqualTo(100000L);
        
        // 할인 가격: 100000 * 0.05 = 5000
        assertThat(order.getDiscountPrice()).isEqualTo(5000L);
    }

    @Test
    @DisplayName("가격이 0인 상품으로 주문을 생성할 수 있다.")
    void createOrderWithZeroPriceProduct() {
        // given
        Long userId = 1L;
        Long userCouponId = 1L;
        double discountRate = 0.1;

        OrderProduct orderProduct = OrderProduct.create(1L, "무료 상품", 0L, 1);
        List<OrderProduct> orderProducts = List.of(orderProduct);

        // when
        Order order = Order.create(userId, userCouponId, orderProducts, discountRate);

        // then
        assertThat(order.getTotalPrice()).isEqualTo(0L);
        assertThat(order.getDiscountPrice()).isEqualTo(0L);
    }

    @Test
    @DisplayName("주문 생성 후 결제 상태를 확인할 수 있다.")
    void checkOrderStatusAfterCreation() {
        // given
        Long userId = 1L;
        Long userCouponId = 1L;
        double discountRate = 0.1;

        OrderProduct orderProduct = OrderProduct.create(1L, "상품", 10000L, 1);
        List<OrderProduct> orderProducts = List.of(orderProduct);

        // when
        Order order = Order.create(userId, userCouponId, orderProducts, discountRate);

        // then
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CREATED);
        
        // 결제 후
        order.paid();
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    @DisplayName("할인율이 소수점인 경우 주문을 생성할 수 있다.")
    void createOrderWithDecimalDiscountRate() {
        // given
        Long userId = 1L;
        Long userCouponId = 1L;
        double discountRate = 0.123; // 12.3% 할인

        OrderProduct orderProduct = OrderProduct.create(1L, "상품", 10000L, 1);
        List<OrderProduct> orderProducts = List.of(orderProduct);

        // when
        Order order = Order.create(userId, userCouponId, orderProducts, discountRate);

        // then
        assertThat(order.getTotalPrice()).isEqualTo(10000L);
        assertThat(order.getDiscountPrice()).isEqualTo(1230L); // 10000 * 0.123 = 1230
    }

    @Test
    @DisplayName("여러 번 결제를 시도해도 상태가 변경되지 않는다.")
    void payOrderMultipleTimes() {
        // given
        Long userId = 1L;
        Long userCouponId = 1L;
        double discountRate = 0.1;

        OrderProduct orderProduct = OrderProduct.create(1L, "상품", 10000L, 1);
        List<OrderProduct> orderProducts = List.of(orderProduct);

        Order order = Order.create(userId, userCouponId, orderProducts, discountRate);

        // when
        order.paid();
        order.paid(); // 두 번째 결제 시도

        // then
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    @DisplayName("주문 상품 목록을 확인할 수 있다.")
    void getOrderProducts() {
        // given
        Long userId = 1L;
        Long userCouponId = 1L;
        double discountRate = 0.1;

        OrderProduct orderProduct1 = OrderProduct.create(1L, "상품1", 10000L, 1);
        OrderProduct orderProduct2 = OrderProduct.create(2L, "상품2", 5000L, 1);
        List<OrderProduct> orderProducts = List.of(orderProduct1, orderProduct2);

        // when
        Order order = Order.create(userId, userCouponId, orderProducts, discountRate);

        // then
        assertThat(order.getOrderProducts()).hasSize(2);
        assertThat(order.getOrderProducts()).contains(orderProduct1, orderProduct2);
    }

    @Test
    @DisplayName("주문 상품이 Order와 양방향 관계를 가지는지 확인한다.")
    void checkOrderProductBidirectionalRelationship() {
        // given
        Long userId = 1L;
        Long userCouponId = 1L;
        double discountRate = 0.1;

        OrderProduct orderProduct = OrderProduct.create(1L, "상품", 10000L, 1);
        List<OrderProduct> orderProducts = List.of(orderProduct);

        // when
        Order order = Order.create(userId, userCouponId, orderProducts, discountRate);

        // then
        assertThat(order.getOrderProducts()).hasSize(1);
        assertThat(orderProduct.getOrder()).isEqualTo(order);
    }

    @Test
    @DisplayName("주문 생성 시 초기 상태가 CREATED인지 확인한다.")
    void checkInitialOrderStatus() {
        // given
        Long userId = 1L;
        Long userCouponId = 1L;
        double discountRate = 0.1;

        OrderProduct orderProduct = OrderProduct.create(1L, "상품", 10000L, 1);
        List<OrderProduct> orderProducts = List.of(orderProduct);

        // when
        Order order = Order.create(userId, userCouponId, orderProducts, discountRate);

        // then
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CREATED);
    }

    @Test
    @DisplayName("주문 ID는 생성 시점에 null인지 확인한다.")
    void checkOrderIdIsNullOnCreation() {
        // given
        Long userId = 1L;
        Long userCouponId = 1L;
        double discountRate = 0.1;

        OrderProduct orderProduct = OrderProduct.create(1L, "상품", 10000L, 1);
        List<OrderProduct> orderProducts = List.of(orderProduct);

        // when
        Order order = Order.create(userId, userCouponId, orderProducts, discountRate);

        // then
        assertThat(order.getId()).isNull(); // ID는 저장 시점에 생성됨
    }
} 