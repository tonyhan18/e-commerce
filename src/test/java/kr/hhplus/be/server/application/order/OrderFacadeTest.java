package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.balance.BalanceService;
import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.product.ProductInfo;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.stock.StockService;
import kr.hhplus.be.server.domain.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderFacadeTest {

    @Mock private UserService userService;
    @Mock private ProductService productService;
    @Mock private OrderService orderService;
    @Mock private BalanceService balanceService;
    @Mock private StockService stockService;
    @Mock private PaymentService paymentService;
    @InjectMocks private OrderFacade orderFacade;

    @Test
    @DisplayName("orderPayment 호출 시 모든 서비스가 순차적으로 호출된다.")
    void orderPayment() {
        // given
        OrderCriteria.OrderPayment criteria = mock(OrderCriteria.OrderPayment.class);
        when(criteria.getUserId()).thenReturn(1L);
        when(criteria.toProductCommand()).thenReturn(null);
        when(criteria.toOrderCommand(any())).thenReturn(mock(OrderCommand.Create.class));
        when(criteria.toBalanceCommand(anyLong())).thenReturn(null);
        when(criteria.toStockCommand()).thenReturn(null);
        when(criteria.toPaymentCommand(any())).thenReturn(null);

        ProductInfo.OrderProducts orderProducts = mock(ProductInfo.OrderProducts.class);
        when(productService.getOrderProducts(any())).thenReturn(orderProducts);

        OrderCommand.Create orderCommand = mock(OrderCommand.Create.class);
        when(criteria.toOrderCommand(orderProducts)).thenReturn(orderCommand);

        OrderInfo.Order order = mock(OrderInfo.Order.class);
        when(orderService.createOrder(orderCommand)).thenReturn(order);
        when(order.getTotalPrice()).thenReturn(10000L);
        when(order.getOrderId()).thenReturn(123L);

        // when
        orderFacade.orderPayment(criteria);

        // then
        verify(userService, times(1)).getUser(1L);
        verify(productService, times(1)).getOrderProducts(any());
        verify(orderService, times(1)).createOrder(orderCommand);
        verify(balanceService, times(1)).useBalance(any());
        verify(stockService, times(1)).deductStock(any());
        verify(paymentService, times(1)).pay(any());
        verify(orderService, times(1)).paidOrder(123L);
    }
}