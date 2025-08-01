package kr.hhplus.be.server.domain.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceIntegrationTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderExternalClient orderExternalClient;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("주문 생성 - 성공")
    void createOrder_success() {
        // given
        List<OrderCommand.OrderProduct> orderProducts = List.of(
            OrderCommand.OrderProduct.of(1L, "상품1", 10000L, 2),
            OrderCommand.OrderProduct.of(2L, "상품2", 5000L, 1)
        );
        OrderCommand.Create command = OrderCommand.Create.of(1L, orderProducts, null, 0.0);
        
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        OrderInfo.Order result = orderService.createOrder(command);

        // then
        assertThat(result).isNotNull();
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("인기 상품 조회 - 성공")
    void getTopPaidProducts_success() {
        // given
        List<Long> orderIds = List.of(1L, 2L);
        OrderCommand.TopOrders command = OrderCommand.TopOrders.of(orderIds, 10);
        
        List<OrderProduct> orderProducts = List.of(
            mock(OrderProduct.class),
            mock(OrderProduct.class),
            mock(OrderProduct.class)
        );
        
        when(orderProducts.get(0).getProductId()).thenReturn(1L);
        when(orderProducts.get(0).getQuantity()).thenReturn(3);
        when(orderProducts.get(1).getProductId()).thenReturn(2L);
        when(orderProducts.get(1).getQuantity()).thenReturn(1);
        when(orderProducts.get(2).getProductId()).thenReturn(1L);
        when(orderProducts.get(2).getQuantity()).thenReturn(2);
        
        when(orderRepository.findOrderIdsIn(orderIds)).thenReturn(orderProducts);

        // when
        OrderInfo.TopPaidProducts result = orderService.getTopPaidProducts(command);

        // then
        assertThat(result).isNotNull();
        verify(orderRepository, times(1)).findOrderIdsIn(orderIds);
    }

    @Test
    @DisplayName("인기 상품 조회 - 빈 결과")
    void getTopPaidProducts_empty() {
        // given
        List<Long> orderIds = List.of(1L, 2L);
        OrderCommand.TopOrders command = OrderCommand.TopOrders.of(orderIds, 10);
        List<OrderProduct> orderProducts = List.of();
        when(orderRepository.findOrderIdsIn(orderIds)).thenReturn(orderProducts);

        // when
        OrderInfo.TopPaidProducts result = orderService.getTopPaidProducts(command);

        // then
        assertThat(result).isNotNull();
        verify(orderRepository, times(1)).findOrderIdsIn(orderIds);
    }

    @Test
    @DisplayName("주문 결제 완료 처리 - 성공")
    void paidOrder_success() {
        // given
        Long orderId = 1L;
        Order mockOrder = mock(Order.class);
        when(orderRepository.findById(orderId)).thenReturn(mockOrder);
        doNothing().when(orderExternalClient).sendOrderMessage(any(Order.class));

        // when
        orderService.paidOrder(orderId);

        // then
        verify(orderRepository, times(1)).findById(orderId);
        verify(mockOrder, times(1)).paid();
        verify(orderExternalClient, times(1)).sendOrderMessage(mockOrder);
    }

    @Test
    @DisplayName("주문 생성 - 할인 쿠폰 적용")
    void createOrder_withDiscount() {
        // given
        List<OrderCommand.OrderProduct> orderProducts = List.of(
            OrderCommand.OrderProduct.of(1L, "상품1", 10000L, 1)
        );
        OrderCommand.Create command = OrderCommand.Create.of(1L, orderProducts, 1L, 0.1); // 10% 할인
        
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        OrderInfo.Order result = orderService.createOrder(command);

        // then
        assertThat(result).isNotNull();
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("인기 상품 조회 - 동일 수량의 상품들")
    void getTopPaidProducts_sameQuantity() {
        // given
        List<Long> orderIds = List.of(1L, 2L);
        OrderCommand.TopOrders command = OrderCommand.TopOrders.of(orderIds, 10);
        
        List<OrderProduct> orderProducts = List.of(
            mock(OrderProduct.class),
            mock(OrderProduct.class)
        );
        
        when(orderProducts.get(0).getProductId()).thenReturn(1L);
        when(orderProducts.get(0).getQuantity()).thenReturn(2);
        when(orderProducts.get(1).getProductId()).thenReturn(2L);
        when(orderProducts.get(1).getQuantity()).thenReturn(2);
        
        when(orderRepository.findOrderIdsIn(orderIds)).thenReturn(orderProducts);

        // when
        OrderInfo.TopPaidProducts result = orderService.getTopPaidProducts(command);

        // then
        assertThat(result).isNotNull();
        verify(orderRepository, times(1)).findOrderIdsIn(orderIds);
    }
} 