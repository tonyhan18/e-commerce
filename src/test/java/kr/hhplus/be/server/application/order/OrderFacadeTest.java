package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.balance.BalanceService;
import kr.hhplus.be.server.domain.coupon.CouponInfo;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.product.ProductInfo;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.stock.StockService;
import kr.hhplus.be.server.domain.user.UserCouponInfo;
import kr.hhplus.be.server.domain.user.UserCouponService;
import kr.hhplus.be.server.domain.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import kr.hhplus.be.server.domain.stock.StockCommand;
import kr.hhplus.be.server.domain.stock.StockCommand.OrderProducts;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
class OrderFacadeTest {

    @Mock private UserService userService;
    @Mock private ProductService productService;
    @Mock private OrderService orderService;
    @Mock private BalanceService balanceService;
    @Mock private StockService stockService;
    @Mock private PaymentService paymentService;
    @Mock private UserCouponService userCouponService;
    @Mock private CouponService couponService;
    @InjectMocks private OrderFacade orderFacade;

    @Test
    @DisplayName("orderPayment 호출 시 모든 서비스가 순차적으로 호출된다.")
    void orderPayment() {
        // given
        OrderCriteria.OrderPayment criteria = mock(OrderCriteria.OrderPayment.class);
        when(criteria.getUserId()).thenReturn(1L);
        when(criteria.getUserCouponId()).thenReturn(null);
        when(criteria.toProductCommand()).thenReturn(null);
        when(criteria.toBalanceCommand(anyLong())).thenReturn(null);
        when(criteria.toStockCommand()).thenReturn(null);
        when(criteria.toPaymentCommand(any())).thenReturn(null);

        ProductInfo.OrderProducts orderProducts = mock(ProductInfo.OrderProducts.class);
        when(productService.getOrderProducts(any())).thenReturn(orderProducts);

        OrderCommand.Create orderCommand = mock(OrderCommand.Create.class);
        when(criteria.toOrderCommand(orderProducts, null, 0.0)).thenReturn(orderCommand);

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

    @Test
    @DisplayName("쿠폰이 있는 주문 결제 시 쿠폰 관련 서비스가 호출된다.")
    void orderPaymentWithCoupon() {
        // given
        OrderCriteria.OrderPayment criteria = mock(OrderCriteria.OrderPayment.class);
        when(criteria.getUserId()).thenReturn(1L);
        when(criteria.getUserCouponId()).thenReturn(1L);
        when(criteria.toProductCommand()).thenReturn(null);
        when(criteria.toCouponCommand()).thenReturn(null);
        when(criteria.toBalanceCommand(anyLong())).thenReturn(null);
        when(criteria.toStockCommand()).thenReturn(null);
        when(criteria.toPaymentCommand(any())).thenReturn(null);

        ProductInfo.OrderProducts orderProducts = mock(ProductInfo.OrderProducts.class);
        when(productService.getOrderProducts(any())).thenReturn(orderProducts);

        UserCouponInfo.UsableCoupon usableCoupon = mock(UserCouponInfo.UsableCoupon.class);
        when(usableCoupon.getUserCouponId()).thenReturn(1L);
        when(userCouponService.getUsableCoupon(any())).thenReturn(usableCoupon);

        CouponInfo.Coupon coupon = mock(CouponInfo.Coupon.class);
        when(coupon.getDiscountRate()).thenReturn(0.1);
        when(couponService.getCoupon(1L)).thenReturn(coupon);

        OrderCommand.Create orderCommand = mock(OrderCommand.Create.class);
        when(criteria.toOrderCommand(orderProducts, 1L, 0.1)).thenReturn(orderCommand);

        OrderInfo.Order order = mock(OrderInfo.Order.class);
        when(orderService.createOrder(orderCommand)).thenReturn(order);
        when(order.getTotalPrice()).thenReturn(9000L);
        when(order.getOrderId()).thenReturn(123L);

        // when
        orderFacade.orderPayment(criteria);

        // then
        verify(userService, times(1)).getUser(1L);
        verify(productService, times(1)).getOrderProducts(any());
        verify(userCouponService, times(1)).getUsableCoupon(any());
        verify(couponService, times(1)).getCoupon(1L);
        verify(orderService, times(1)).createOrder(orderCommand);
        verify(balanceService, times(1)).useBalance(any());
        verify(userCouponService, times(1)).useUserCoupon(1L);
        verify(stockService, times(1)).deductStock(any());
        verify(paymentService, times(1)).pay(any());
        verify(orderService, times(1)).paidOrder(123L);
    }

    @Test
    @DisplayName("결제 실패 시 재고가 복구된다.")
    void orderPayment_whenPaymentFails_thenStockIsRestored() {
        // given
        OrderCriteria.OrderPayment criteria = mock(OrderCriteria.OrderPayment.class);
        when(criteria.getUserId()).thenReturn(1L);
        when(criteria.getUserCouponId()).thenReturn(null);
        when(criteria.toBalanceCommand(anyLong())).thenReturn(null);

        ProductInfo.OrderProducts orderProducts = mock(ProductInfo.OrderProducts.class);
        when(productService.getOrderProducts(any())).thenReturn(orderProducts);

        StockCommand.OrderProducts stockCommand = mock(StockCommand.OrderProducts.class);
        when(criteria.toStockCommand()).thenReturn(stockCommand);

        OrderInfo.Order order = mock(OrderInfo.Order.class);
        when(orderService.createOrder(any())).thenReturn(order);
        when(order.getTotalPrice()).thenReturn(10000L);

        doThrow(new RuntimeException("결제 실패!")).when(paymentService).pay(any());

        // when & then
        try {
            orderFacade.orderPayment(criteria);
        } catch (Exception e) {
            // then
            verify(stockService, times(1)).addStock(stockCommand);
            verify(stockService, times(1)).deductStock(stockCommand);
            verify(balanceService, times(1)).useBalance(any());
            verify(paymentService, times(1)).pay(any());
            verify(orderService, never()).paidOrder(any());
            return;
        }
        fail("예외가 발생해야 합니다.");
    }
}