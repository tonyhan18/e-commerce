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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderFacadeIntegrationTest {

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
    void orderPayment_success_withoutCoupon() {
        // given
        Long userId = 1L;
        List<OrderCriteria.OrderProduct> products = List.of(
            OrderCriteria.OrderProduct.of(1L, 2),
            OrderCriteria.OrderProduct.of(2L, 1)
        );
        OrderCriteria.OrderPayment criteria = OrderCriteria.OrderPayment.of(userId, products, null);

        List<ProductInfo.OrderProduct> orderProducts = List.of(
            mock(ProductInfo.OrderProduct.class),
            mock(ProductInfo.OrderProduct.class)
        );
        ProductInfo.OrderProducts productInfo = mock(ProductInfo.OrderProducts.class);
        when(productInfo.getProducts()).thenReturn(orderProducts);
        when(productService.getOrderProducts(any())).thenReturn(productInfo);

        ProductInfo.OrderProduct product1 = orderProducts.get(0);
        when(product1.getProductId()).thenReturn(1L);
        when(product1.getProductName()).thenReturn("상품1");
        when(product1.getProductPrice()).thenReturn(10000L);
        when(product1.getQuantity()).thenReturn(2);

        ProductInfo.OrderProduct product2 = orderProducts.get(1);
        when(product2.getProductId()).thenReturn(2L);
        when(product2.getProductName()).thenReturn("상품2");
        when(product2.getProductPrice()).thenReturn(20000L);
        when(product2.getQuantity()).thenReturn(1);

        OrderInfo.Order order = mock(OrderInfo.Order.class);
        when(order.getOrderId()).thenReturn(1L);
        when(order.getTotalPrice()).thenReturn(40000L);
        when(order.getDiscountPrice()).thenReturn(0L);
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
        verify(userCouponService, never()).getUsableCoupon(any());
        verify(couponService, never()).getCoupon(any());
        verify(userCouponService, never()).useUserCoupon(any());
    }

    @Test
    @DisplayName("주문 결제 - 성공 (쿠폰 사용)")
    void orderPayment_success_withCoupon() {
        // given
        Long userId = 1L;
        Long userCouponId = 1L;
        List<OrderCriteria.OrderProduct> products = List.of(
            OrderCriteria.OrderProduct.of(1L, 1)
        );
        OrderCriteria.OrderPayment criteria = OrderCriteria.OrderPayment.of(userId, products, userCouponId);

        List<ProductInfo.OrderProduct> orderProducts = List.of(
            mock(ProductInfo.OrderProduct.class)
        );
        ProductInfo.OrderProducts productInfo = mock(ProductInfo.OrderProducts.class);
        when(productInfo.getProducts()).thenReturn(orderProducts);
        when(productService.getOrderProducts(any())).thenReturn(productInfo);

        ProductInfo.OrderProduct product = orderProducts.get(0);
        when(product.getProductId()).thenReturn(1L);
        when(product.getProductName()).thenReturn("상품1");
        when(product.getProductPrice()).thenReturn(10000L);
        when(product.getQuantity()).thenReturn(1);

        UserCouponInfo.UsableCoupon usableCoupon = mock(UserCouponInfo.UsableCoupon.class);
        when(usableCoupon.getUserCouponId()).thenReturn(userCouponId);
        when(userCouponService.getUsableCoupon(any())).thenReturn(usableCoupon);

        CouponInfo.Coupon coupon = mock(CouponInfo.Coupon.class);
        when(coupon.getDiscountRate()).thenReturn(0.1); // 10% 할인
        when(couponService.getCoupon(userCouponId)).thenReturn(coupon);

        OrderInfo.Order order = mock(OrderInfo.Order.class);
        when(order.getOrderId()).thenReturn(1L);
        when(order.getTotalPrice()).thenReturn(9000L); // 할인 적용된 가격
        when(order.getDiscountPrice()).thenReturn(1000L);
        when(orderService.createOrder(any())).thenReturn(order);

        // when
        OrderResult.Order result = orderFacade.orderPayment(criteria);

        // then
        assertThat(result).isNotNull();
        verify(userService, times(1)).getUser(userId);
        verify(productService, times(1)).getOrderProducts(any());
        verify(userCouponService, times(1)).getUsableCoupon(any());
        verify(couponService, times(1)).getCoupon(userCouponId);
        verify(orderService, times(1)).createOrder(any());
        verify(balanceService, times(1)).useBalance(any());
        verify(userCouponService, times(1)).useUserCoupon(userCouponId);
        verify(stockService, times(1)).deductStock(any());
        verify(paymentService, times(1)).pay(any());
        verify(orderService, times(1)).paidOrder(1L);
    }

    @Test
    @DisplayName("주문 결제 - 사용자 없음")
    void orderPayment_userNotFound() {
        // given
        Long userId = 999L;
        List<OrderCriteria.OrderProduct> products = List.of(
            OrderCriteria.OrderProduct.of(1L, 1)
        );
        OrderCriteria.OrderPayment criteria = OrderCriteria.OrderPayment.of(userId, products, null);

        doThrow(new IllegalArgumentException("User not found"))
            .when(userService).getUser(userId);

        // when & then
        assertThatThrownBy(() -> orderFacade.orderPayment(criteria))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("User not found");

        verify(userService, times(1)).getUser(userId);
        verify(productService, never()).getOrderProducts(any());
        verify(orderService, never()).createOrder(any());
        verify(balanceService, never()).useBalance(any());
        verify(stockService, never()).deductStock(any());
        verify(paymentService, never()).pay(any());
        verify(orderService, never()).paidOrder(any());
    }

    @Test
    @DisplayName("주문 결제 - 결제 실패 시 재고 복구")
    void orderPayment_paymentFailure_stockRecovery() {
        // given
        Long userId = 1L;
        List<OrderCriteria.OrderProduct> products = List.of(
            OrderCriteria.OrderProduct.of(1L, 1)
        );
        OrderCriteria.OrderPayment criteria = OrderCriteria.OrderPayment.of(userId, products, null);

        List<ProductInfo.OrderProduct> orderProducts = List.of(
            mock(ProductInfo.OrderProduct.class)
        );
        ProductInfo.OrderProducts productInfo = mock(ProductInfo.OrderProducts.class);
        when(productInfo.getProducts()).thenReturn(orderProducts);
        when(productService.getOrderProducts(any())).thenReturn(productInfo);

        ProductInfo.OrderProduct product = orderProducts.get(0);
        when(product.getProductId()).thenReturn(1L);
        when(product.getProductName()).thenReturn("상품1");
        when(product.getProductPrice()).thenReturn(10000L);
        when(product.getQuantity()).thenReturn(1);

        OrderInfo.Order order = mock(OrderInfo.Order.class);
        when(order.getTotalPrice()).thenReturn(10000L);
        when(orderService.createOrder(any())).thenReturn(order);

        doThrow(new RuntimeException("Payment failed"))
            .when(paymentService).pay(any());

        // when & then
        assertThatThrownBy(() -> orderFacade.orderPayment(criteria))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Payment failed");

        verify(userService, times(1)).getUser(userId);
        verify(productService, times(1)).getOrderProducts(any());
        verify(orderService, times(1)).createOrder(any());
        verify(balanceService, times(1)).useBalance(any());
        verify(stockService, times(1)).deductStock(any());
        verify(paymentService, times(1)).pay(any());
        verify(stockService, times(1)).addStock(any()); // 재고 복구
        verify(orderService, never()).paidOrder(any());
    }

    @Test
    @DisplayName("주문 결제 - 쿠폰 사용 불가")
    void orderPayment_couponNotUsable() {
        // given
        Long userId = 1L;
        Long userCouponId = 1L;
        List<OrderCriteria.OrderProduct> products = List.of(
            OrderCriteria.OrderProduct.of(1L, 1)
        );
        OrderCriteria.OrderPayment criteria = OrderCriteria.OrderPayment.of(userId, products, userCouponId);

        ProductInfo.OrderProducts productInfo = mock(ProductInfo.OrderProducts.class);
        when(productService.getOrderProducts(any())).thenReturn(productInfo);

        doThrow(new IllegalStateException("사용할 수 없는 쿠폰입니다."))
            .when(userCouponService).getUsableCoupon(any());

        // when & then
        assertThatThrownBy(() -> orderFacade.orderPayment(criteria))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("사용할 수 없는 쿠폰입니다.");

        verify(userService, times(1)).getUser(userId);
        verify(productService, times(1)).getOrderProducts(any());
        verify(userCouponService, times(1)).getUsableCoupon(any());
        verify(couponService, never()).getCoupon(any());
        verify(orderService, never()).createOrder(any());
        verify(balanceService, never()).useBalance(any());
        verify(stockService, never()).deductStock(any());
        verify(paymentService, never()).pay(any());
        verify(orderService, never()).paidOrder(any());
    }
} 