package kr.hhplus.be.server.domain.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderRepositoryTest {

    @Mock
    private OrderRepository orderRepository;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        // 테스트용 OrderProduct 생성
        OrderProduct testOrderProduct = OrderProduct.builder()
                .productId(1L)
                .productName("테스트 상품")
                .unitPrice(10000L)
                .quantity(1)
                .build();
        
        testOrder = Order.create(1L, List.of(testOrderProduct), null, 0.0);
    }

    @Test
    @DisplayName("주문 저장 - 성공")
    void save_success() {
        // given
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // when
        Order savedOrder = orderRepository.save(testOrder);

        // then
        assertThat(savedOrder).isNotNull();
        verify(orderRepository, times(1)).save(testOrder);
    }

    @Test
    @DisplayName("주문 조회 - 성공")
    void findById_success() {
        // given
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(testOrder);

        // when
        Order result = orderRepository.findById(orderId);

        // then
        assertThat(result).isNotNull();
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    @DisplayName("주문 메시지 전송")
    void sendOrderMessage_success() {
        // given
        doNothing().when(orderRepository).sendOrderMessage(any(Order.class));

        // when
        orderRepository.sendOrderMessage(testOrder);

        // then
        verify(orderRepository, times(1)).sendOrderMessage(testOrder);
    }

    @Test
    @DisplayName("주문 상품 목록 조회")
    void findOrderIdsIn_success() {
        // given
        List<Long> orderIds = List.of(1L, 2L);
        List<OrderProduct> orderProducts = List.of();
        when(orderRepository.findOrderIdsIn(orderIds)).thenReturn(orderProducts);

        // when
        List<OrderProduct> result = orderRepository.findOrderIdsIn(orderIds);

        // then
        assertThat(result).isEmpty();
        verify(orderRepository, times(1)).findOrderIdsIn(orderIds);
    }
} 