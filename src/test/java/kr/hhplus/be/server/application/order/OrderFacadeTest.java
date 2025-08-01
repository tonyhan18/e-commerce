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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderFacadeTest {

    @Mock
    private UserService userService;

    @Mock
    private ProductService productService;

    @Mock
    private OrderService orderService;

    @Mock
    private BalanceService balanceService;

    @Mock
    private StockService stockService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private UserCouponService userCouponService;

    @Mock
    private CouponService couponService;

    @InjectMocks
    private OrderFacade orderFacade;

    @Test
    @DisplayName("주문 결제 - 성공 (쿠폰 없음)")
    void orderPayment_successWithoutCoupon() {
        // given
        Long userId = 1L;
        Long totalPrice = 25000L;
        OrderCriteria.OrderPayment criteria = mock(OrderCriteria.OrderPayment.class);
        when(criteria.getUserId()).thenReturn(userId);
        when(criteria.getUserCouponId()).thenReturn(null);

        ProductInfo.OrderProducts orderProducts = mock(ProductInfo.OrderProducts.class);
        when(productService.getOrderProducts(any())).thenReturn(orderProducts);

        OrderInfo.Order order = mock(OrderInfo.Order.class);
        when(order.getTotalPrice()).thenReturn(totalPrice);
        when(order.getOrderId()).thenReturn(1L);
        when(orderService.createOrder(any())).thenReturn(order);

        // when
        OrderResult.Order result = orderFacade.orderPayment(criteria);

        // then
        assertThat(result).isNotNull();
        verify(userService, times(1)).getUser(userId);
        verify(productService, times(1)).getOrderProducts(any());
        verify(orderService, times(1)).createOrder(any());
        verify(balanceService, times(1)).useBalance(any());
        verify(stockService, times(1)).deductStock(any());
        verify(paymentService, times(1)).pay(any());
        verify(orderService, times(1)).paidOrder(1L);
    }

    @Test
    @DisplayName("주문 결제 - 성공 (쿠폰 적용)")
    void orderPayment_successWithCoupon() {
        // given
        Long userId = 1L;
        Long userCouponId = 1L;
        Long couponId = 1L;
        Long totalPrice = 25000L;
        double discountRate = 0.1;

        OrderCriteria.OrderPayment criteria = mock(OrderCriteria.OrderPayment.class);
        when(criteria.getUserId()).thenReturn(userId);
        when(criteria.getUserCouponId()).thenReturn(userCouponId);

        ProductInfo.OrderProducts orderProducts = mock(ProductInfo.OrderProducts.class);
        when(productService.getOrderProducts(any())).thenReturn(orderProducts);

        UserCouponInfo.UsableCoupon usableCoupon = mock(UserCouponInfo.UsableCoupon.class);
        when(usableCoupon.getUserCouponId()).thenReturn(userCouponId);
        when(userCouponService.getUsableCoupon(any())).thenReturn(usableCoupon);

        CouponInfo.Coupon coupon = mock(CouponInfo.Coupon.class);
        when(coupon.getDiscountRate()).thenReturn(discountRate);
        when(couponService.getCoupon(couponId)).thenReturn(coupon);

        OrderInfo.Order order = mock(OrderInfo.Order.class);
        when(order.getTotalPrice()).thenReturn(totalPrice);
        when(order.getOrderId()).thenReturn(1L);
        when(orderService.createOrder(any())).thenReturn(order);

        // when
        OrderResult.Order result = orderFacade.orderPayment(criteria);

        // then
        assertThat(result).isNotNull();
        verify(userService, times(1)).getUser(userId);
        verify(productService, times(1)).getOrderProducts(any());
        verify(userCouponService, times(1)).getUsableCoupon(any());
        verify(couponService, times(1)).getCoupon(couponId);
        verify(orderService, times(1)).createOrder(any());
        verify(balanceService, times(1)).useBalance(any());
        verify(userCouponService, times(1)).useUserCoupon(userCouponId);
        verify(stockService, times(1)).deductStock(any());
        verify(paymentService, times(1)).pay(any());
        verify(orderService, times(1)).paidOrder(1L);
    }

    @Test
    @DisplayName("주문 결제 - 결제 실패 시 재고 복구")
    void orderPayment_paymentFailureRecoverStock() {
        // given
        Long userId = 1L;
        Long totalPrice = 25000L;
        OrderCriteria.OrderPayment criteria = mock(OrderCriteria.OrderPayment.class);
        when(criteria.getUserId()).thenReturn(userId);
        when(criteria.getUserCouponId()).thenReturn(null);

        ProductInfo.OrderProducts orderProducts = mock(ProductInfo.OrderProducts.class);
        when(productService.getOrderProducts(any())).thenReturn(orderProducts);

        OrderInfo.Order order = mock(OrderInfo.Order.class);
        when(order.getTotalPrice()).thenReturn(totalPrice);
        when(orderService.createOrder(any())).thenReturn(order);

        doThrow(new RuntimeException("결제 실패"))
            .when(paymentService).pay(any());

        // when & then
        assertThatThrownBy(() -> orderFacade.orderPayment(criteria))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("결제 실패");

        verify(userService, times(1)).getUser(userId);
        verify(productService, times(1)).getOrderProducts(any());
        verify(orderService, times(1)).createOrder(any());
        verify(balanceService, times(1)).useBalance(any());
        verify(stockService, times(1)).deductStock(any());
        verify(paymentService, times(1)).pay(any());
        verify(stockService, times(1)).addStock(any()); // 재고 복구
        verify(orderService, never()).paidOrder(any()); // 결제 실패로 paidOrder 호출 안됨
    }
} 