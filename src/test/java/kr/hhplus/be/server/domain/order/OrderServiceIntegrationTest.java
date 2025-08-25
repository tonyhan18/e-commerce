package kr.hhplus.be.server.domain.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kr.hhplus.be.server.support.IntegrationTestSupport;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class OrderServiceIntegrationTest extends IntegrationTestSupport{

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    @DisplayName("주문을 생성한다.")
    @Test
    void createOrder() {
        // given
        OrderCommand.Create command = OrderCommand.Create.of(1L, List.of(
            OrderCommand.OrderProduct.of(1L, "상품1", 10_000L, 2),
            OrderCommand.OrderProduct.of(2L, "상품2", 20_000L, 3)
        ), 1L, 0.1);

        // when
        OrderInfo.Order order = orderService.createOrder(command);

        // then
        assertThat(order.getOrderId()).isNotNull();
        assertThat(order.getTotalPrice()).isEqualTo(72_000L);
        assertThat(order.getDiscountPrice()).isEqualTo(8_000L);
    }

    @DisplayName("주문을 결제완료처리 한다.")
    @Test
    void paidOrder() {
        // given
        Order order = Order.create(1L, 1L, 0.1, List.of(
            OrderProduct.create(1L, "상품1", 10_000L, 2),
            OrderProduct.create(2L, "상품2", 20_000L, 3)
        ));
        orderRepository.save(order);

        // when
        orderService.paidOrder(order.getId());

        // then
        Order result = orderRepository.findById(order.getId());
        assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.PAID);
    }
} 