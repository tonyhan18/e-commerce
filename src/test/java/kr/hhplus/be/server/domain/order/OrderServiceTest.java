package kr.hhplus.be.server.domain.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderExternalClient orderExternalClient;

    @InjectMocks
    private OrderService orderService;

    private List<OrderProduct> mockOrderProducts;

    @BeforeEach
    void setUp() {
        mockOrderProducts = Arrays.asList(
                OrderProduct.create(1L, "상품1", 10000L, 2),
                OrderProduct.create(2L, "상품2", 5000L, 1)
        );
    }

    @Test
    @DisplayName("주문을 생성할 수 있다.")
    void createOrder() {
        // given
        OrderCommand.OrderProduct orderProduct1 = mock(OrderCommand.OrderProduct.class);
        when(orderProduct1.getProductId()).thenReturn(1L);
        when(orderProduct1.getProductName()).thenReturn("상품1");
        when(orderProduct1.getProductPrice()).thenReturn(10000L);
        when(orderProduct1.getQuantity()).thenReturn(2);

        OrderCommand.OrderProduct orderProduct2 = mock(OrderCommand.OrderProduct.class);
        when(orderProduct2.getProductId()).thenReturn(2L);
        when(orderProduct2.getProductName()).thenReturn("상품2");
        when(orderProduct2.getProductPrice()).thenReturn(5000L);
        when(orderProduct2.getQuantity()).thenReturn(1);

        OrderCommand.Create command = mock(OrderCommand.Create.class);
        when(command.getUserId()).thenReturn(1L);
        when(command.getUserCouponId()).thenReturn(null);
        when(command.getDiscountRate()).thenReturn(0.0);
        when(command.getProducts()).thenReturn(Arrays.asList(orderProduct1, orderProduct2));

        // when
        OrderInfo.Order result = orderService.createOrder(command);

        // then
        assertThat(result).isNotNull();
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("인기 상품 목록을 조회할 수 있다.")
    void getTopPaidProducts() {
        // given
        OrderCommand.TopOrders topOrders = mock(OrderCommand.TopOrders.class);
        when(topOrders.getOrderIds()).thenReturn(Arrays.asList(1L, 2L, 3L));
        when(orderRepository.findOrderIdsIn(anyList())).thenReturn(mockOrderProducts);

        // when
        OrderInfo.TopPaidProducts result = orderService.getTopPaidProducts(topOrders);

        // then
        assertThat(result).isNotNull();
        verify(orderRepository, times(1)).findOrderIdsIn(topOrders.getOrderIds());
    }

    @Test
    @DisplayName("주문을 결제 완료 상태로 변경할 수 있다.")
    void paidOrder() {
        // given
        Long orderId = 1L;
        Order mockOrder = mock(Order.class);
        when(orderRepository.findById(orderId)).thenReturn(mockOrder);
        doNothing().when(orderExternalClient).sendOrderMessage(any(Order.class));

        // when
        orderService.paidOrder(orderId);

        // then
        verify(mockOrder, times(1)).paid();
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderExternalClient, times(1)).sendOrderMessage(mockOrder);
    }
}
