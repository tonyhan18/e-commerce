package kr.hhplus.be.server.domain.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import kr.hhplus.be.server.MockTestSupport;

class OrderServiceTest extends MockTestSupport {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("주문을 생성할 수 있다.")
    void createOrder() {
        // given
        Long userId = 1L;
        Long userCouponId = 100L;
        double discountRate = 0.1;

        OrderCommand.OrderProduct orderProductCommand1 = OrderCommand.OrderProduct.of(1L, "상품1", 10000L, 2);
        OrderCommand.OrderProduct orderProductCommand2 = OrderCommand.OrderProduct.of(2L, "상품2", 20000L, 1);
        List<OrderCommand.OrderProduct> products = List.of(orderProductCommand1, orderProductCommand2);

        OrderCommand.Create createCommand = OrderCommand.Create.of(userId, userCouponId, discountRate, products);

        Order savedOrder = Order.create(userId, userCouponId, 
            List.of(
                OrderProduct.create(1L, "상품1", 10000L, 2),
                OrderProduct.create(2L, "상품2", 20000L, 1)
            ), 
            discountRate
        );
        savedOrder.paid(); // ID 설정을 위해 paid() 호출

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // when
        OrderInfo.Order result = orderService.createOrder(createCommand);

        // then
        assertThat(result.getOrderId()).isEqualTo(savedOrder.getId());
        assertThat(result.getTotalPrice()).isEqualTo(savedOrder.getTotalPrice());
        assertThat(result.getDiscountPrice()).isEqualTo(savedOrder.getDiscountPrice());

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("단일 상품으로 주문을 생성할 수 있다.")
    void createOrderWithSingleProduct() {
        // given
        Long userId = 1L;
        Long userCouponId = null;
        double discountRate = 0.0;

        OrderCommand.OrderProduct orderProductCommand = OrderCommand.OrderProduct.of(1L, "단일 상품", 15000L, 1);
        List<OrderCommand.OrderProduct> products = List.of(orderProductCommand);

        OrderCommand.Create createCommand = OrderCommand.Create.of(userId, userCouponId, discountRate, products);

        Order savedOrder = Order.create(userId, userCouponId, 
            List.of(OrderProduct.create(1L, "단일 상품", 15000L, 1)), 
            discountRate
        );
        savedOrder.paid();

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // when
        OrderInfo.Order result = orderService.createOrder(createCommand);

        // then
        assertThat(result.getOrderId()).isEqualTo(savedOrder.getId());
        assertThat(result.getTotalPrice()).isEqualTo(15000L);
        assertThat(result.getDiscountPrice()).isEqualTo(15000L); // 할인율 0%

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("할인이 적용된 주문을 생성할 수 있다.")
    void createOrderWithDiscount() {
        // given
        Long userId = 1L;
        Long userCouponId = 200L;
        double discountRate = 0.2; // 20% 할인

        OrderCommand.OrderProduct orderProductCommand = OrderCommand.OrderProduct.of(1L, "할인 상품", 10000L, 3);
        List<OrderCommand.OrderProduct> products = List.of(orderProductCommand);

        OrderCommand.Create createCommand = OrderCommand.Create.of(userId, userCouponId, discountRate, products);

        Order savedOrder = Order.create(userId, userCouponId, 
            List.of(OrderProduct.create(1L, "할인 상품", 10000L, 3)), 
            discountRate
        );
        savedOrder.paid();

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // when
        OrderInfo.Order result = orderService.createOrder(createCommand);

        // then
        assertThat(result.getOrderId()).isEqualTo(savedOrder.getId());
        assertThat(result.getTotalPrice()).isEqualTo(30000L); // 10000 * 3
        assertThat(result.getDiscountPrice()).isEqualTo(24000L); // 30000 * 0.8

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("쿠폰 없이 주문을 생성할 수 있다.")
    void createOrderWithoutCoupon() {
        // given
        Long userId = 1L;
        Long userCouponId = null;
        double discountRate = 0.0;

        OrderCommand.OrderProduct orderProductCommand = OrderCommand.OrderProduct.of(1L, "쿠폰없음 상품", 5000L, 2);
        List<OrderCommand.OrderProduct> products = List.of(orderProductCommand);

        OrderCommand.Create createCommand = OrderCommand.Create.of(userId, userCouponId, discountRate, products);

        Order savedOrder = Order.create(userId, userCouponId, 
            List.of(OrderProduct.create(1L, "쿠폰없음 상품", 5000L, 2)), 
            discountRate
        );
        savedOrder.paid();

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // when
        OrderInfo.Order result = orderService.createOrder(createCommand);

        // then
        assertThat(result.getOrderId()).isEqualTo(savedOrder.getId());
        assertThat(result.getTotalPrice()).isEqualTo(10000L); // 5000 * 2
        assertThat(result.getDiscountPrice()).isEqualTo(10000L); // 할인 없음

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("주문 결제를 처리할 수 있다.")
    void payOrder() {
        // given
        Long orderId = 1L;
        Order order = Order.create(1L, 100L, 
            List.of(OrderProduct.create(1L, "상품", 10000L, 1)), 
            0.1
        );
        order.paid();

        when(orderRepository.findById(orderId)).thenReturn(order);

        // when
        orderService.payOrder(orderId);

        // then
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).sendOrderMessage(order);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    @DisplayName("주문 결제 시 주문 상태가 PAID로 변경된다.")
    void payOrderChangesStatusToPaid() {
        // given
        Long orderId = 1L;
        Order order = Order.create(1L, null, 
            List.of(OrderProduct.create(1L, "상품", 5000L, 2)), 
            0.0
        );

        when(orderRepository.findById(orderId)).thenReturn(order);

        // when
        orderService.payOrder(orderId);

        // then
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAID);
        verify(orderRepository, times(1)).sendOrderMessage(order);
    }

    @Test
    @DisplayName("인기 상품 순위를 조회할 수 있다.")
    void getTopPayProducts() {
        // given
        List<Long> orderIds = List.of(1L, 2L, 3L);
        OrderCommand.TopOrders topOrdersCommand = OrderCommand.TopOrders.of(orderIds, 5);

        List<OrderProduct> orderProducts = List.of(
            OrderProduct.create(1L, "상품1", 10000L, 3), // 총 3개
            OrderProduct.create(2L, "상품2", 20000L, 2), // 총 2개
            OrderProduct.create(1L, "상품1", 10000L, 1), // 상품1 추가 1개 (총 4개)
            OrderProduct.create(3L, "상품3", 15000L, 1)  // 총 1개
        );

        when(orderRepository.findOrderIdsIn(orderIds)).thenReturn(orderProducts);

        // when
        OrderInfo.TopPaidProducts result = orderService.getTopPayProducts(topOrdersCommand);

        // then
        assertThat(result.getProductIds()).hasSize(3);
        assertThat(result.getProductIds().get(0)).isEqualTo(1L); // 상품1: 4개 (가장 많음)
        assertThat(result.getProductIds().get(1)).isEqualTo(2L); // 상품2: 2개
        assertThat(result.getProductIds().get(2)).isEqualTo(3L); // 상품3: 1개

        verify(orderRepository, times(1)).findOrderIdsIn(orderIds);
    }

    @Test
    @DisplayName("동일한 수량의 상품들이 있을 때 상품 ID 순으로 정렬된다.")
    void getTopPayProductsWithSameQuantity() {
        // given
        List<Long> orderIds = List.of(1L, 2L);
        OrderCommand.TopOrders topOrdersCommand = OrderCommand.TopOrders.of(orderIds, 5);

        List<OrderProduct> orderProducts = List.of(
            OrderProduct.create(3L, "상품3", 10000L, 2), // 상품3: 2개
            OrderProduct.create(1L, "상품1", 20000L, 2), // 상품1: 2개
            OrderProduct.create(2L, "상품2", 15000L, 2)  // 상품2: 2개
        );

        when(orderRepository.findOrderIdsIn(orderIds)).thenReturn(orderProducts);

        // when
        OrderInfo.TopPaidProducts result = orderService.getTopPayProducts(topOrdersCommand);

        // then
        assertThat(result.getProductIds()).hasSize(3);
        // 모든 상품이 동일한 수량(2개)이므로 상품 ID 순으로 정렬됨
        assertThat(result.getProductIds().get(0)).isEqualTo(1L);
        assertThat(result.getProductIds().get(1)).isEqualTo(2L);
        assertThat(result.getProductIds().get(2)).isEqualTo(3L);

        verify(orderRepository, times(1)).findOrderIdsIn(orderIds);
    }

    @Test
    @DisplayName("주문 상품이 없을 때 빈 리스트를 반환한다.")
    void getTopPayProductsWithEmptyOrders() {
        // given
        List<Long> orderIds = List.of(1L, 2L);
        OrderCommand.TopOrders topOrdersCommand = OrderCommand.TopOrders.of(orderIds, 5);

        List<OrderProduct> orderProducts = List.of();

        when(orderRepository.findOrderIdsIn(orderIds)).thenReturn(orderProducts);

        // when
        OrderInfo.TopPaidProducts result = orderService.getTopPayProducts(topOrdersCommand);

        // then
        assertThat(result.getProductIds()).isEmpty();

        verify(orderRepository, times(1)).findOrderIdsIn(orderIds);
    }

    @Test
    @DisplayName("단일 상품만 있는 주문에서 인기 상품을 조회할 수 있다.")
    void getTopPayProductsWithSingleProduct() {
        // given
        List<Long> orderIds = List.of(1L);
        OrderCommand.TopOrders topOrdersCommand = OrderCommand.TopOrders.of(orderIds, 5);

        List<OrderProduct> orderProducts = List.of(
            OrderProduct.create(1L, "단일 상품", 10000L, 5)
        );

        when(orderRepository.findOrderIdsIn(orderIds)).thenReturn(orderProducts);

        // when
        OrderInfo.TopPaidProducts result = orderService.getTopPayProducts(topOrdersCommand);

        // then
        assertThat(result.getProductIds()).hasSize(1);
        assertThat(result.getProductIds().get(0)).isEqualTo(1L);

        verify(orderRepository, times(1)).findOrderIdsIn(orderIds);
    }

    @Test
    @DisplayName("대량의 주문 상품에서 인기 상품을 조회할 수 있다.")
    void getTopPayProductsWithLargeQuantity() {
        // given
        List<Long> orderIds = List.of(1L, 2L);
        OrderCommand.TopOrders topOrdersCommand = OrderCommand.TopOrders.of(orderIds, 5);

        List<OrderProduct> orderProducts = List.of(
            OrderProduct.create(1L, "대량 상품1", 1000L, 1000), // 1000개
            OrderProduct.create(2L, "대량 상품2", 2000L, 500),  // 500개
            OrderProduct.create(1L, "대량 상품1", 1000L, 500)   // 상품1 추가 500개 (총 1500개)
        );

        when(orderRepository.findOrderIdsIn(orderIds)).thenReturn(orderProducts);

        // when
        OrderInfo.TopPaidProducts result = orderService.getTopPayProducts(topOrdersCommand);

        // then
        assertThat(result.getProductIds()).hasSize(2);
        assertThat(result.getProductIds().get(0)).isEqualTo(1L); // 상품1: 1500개
        assertThat(result.getProductIds().get(1)).isEqualTo(2L); // 상품2: 500개

        verify(orderRepository, times(1)).findOrderIdsIn(orderIds);
    }

    @Test
    @DisplayName("주문 상품 생성이 createOrder를 통해 올바르게 동작한다.")
    void createOrderProductThroughCreateOrder() {
        // given
        Long userId = 1L;
        Long userCouponId = null;
        double discountRate = 0.0;

        OrderCommand.OrderProduct orderProductCommand = OrderCommand.OrderProduct.of(1L, "테스트 상품", 10000L, 3);
        List<OrderCommand.OrderProduct> products = List.of(orderProductCommand);

        OrderCommand.Create createCommand = OrderCommand.Create.of(userId, userCouponId, discountRate, products);

        Order savedOrder = Order.create(userId, userCouponId, 
            List.of(OrderProduct.create(1L, "테스트 상품", 10000L, 3)), 
            discountRate
        );
        savedOrder.paid();

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // when
        OrderInfo.Order result = orderService.createOrder(createCommand);

        // then
        assertThat(result.getOrderId()).isEqualTo(savedOrder.getId());
        assertThat(result.getTotalPrice()).isEqualTo(30000L); // 10000 * 3
        assertThat(result.getDiscountPrice()).isEqualTo(30000L); // 할인 없음

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("주문 상품 수량이 0일 때 총 가격이 0이다.")
    void createOrderWithZeroQuantity() {
        // given
        Long userId = 1L;
        Long userCouponId = null;
        double discountRate = 0.0;

        OrderCommand.OrderProduct orderProductCommand = OrderCommand.OrderProduct.of(1L, "수량 0 상품", 10000L, 0);
        List<OrderCommand.OrderProduct> products = List.of(orderProductCommand);

        OrderCommand.Create createCommand = OrderCommand.Create.of(userId, userCouponId, discountRate, products);

        Order savedOrder = Order.create(userId, userCouponId, 
            List.of(OrderProduct.create(1L, "수량 0 상품", 10000L, 0)), 
            discountRate
        );
        savedOrder.paid();

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // when
        OrderInfo.Order result = orderService.createOrder(createCommand);

        // then
        assertThat(result.getTotalPrice()).isEqualTo(0L); // 10000 * 0
        assertThat(result.getDiscountPrice()).isEqualTo(0L);

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("주문 상품 단가가 0일 때 총 가격이 0이다.")
    void createOrderWithZeroPrice() {
        // given
        Long userId = 1L;
        Long userCouponId = null;
        double discountRate = 0.0;

        OrderCommand.OrderProduct orderProductCommand = OrderCommand.OrderProduct.of(1L, "무료 상품", 0L, 5);
        List<OrderCommand.OrderProduct> products = List.of(orderProductCommand);

        OrderCommand.Create createCommand = OrderCommand.Create.of(userId, userCouponId, discountRate, products);

        Order savedOrder = Order.create(userId, userCouponId, 
            List.of(OrderProduct.create(1L, "무료 상품", 0L, 5)), 
            discountRate
        );
        savedOrder.paid();

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // when
        OrderInfo.Order result = orderService.createOrder(createCommand);

        // then
        assertThat(result.getTotalPrice()).isEqualTo(0L); // 0 * 5
        assertThat(result.getDiscountPrice()).isEqualTo(0L);

        verify(orderRepository, times(1)).save(any(Order.class));
    }
} 