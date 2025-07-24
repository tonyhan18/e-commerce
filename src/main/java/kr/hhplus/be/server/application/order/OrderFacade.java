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
import kr.hhplus.be.server.domain.user.UserCouponInfo.Coupon;
import kr.hhplus.be.server.domain.user.UserCouponService;
import kr.hhplus.be.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderFacade {

    private static final double NOT_DISCOUNT = 0.0;

    private final UserService userService;
    private final ProductService productService;
    private final OrderService orderService;
    private final BalanceService balanceService;
    private final StockService stockService;
    private final PaymentService paymentService;
    private final UserCouponService userCouponService;
    private final CouponService couponService;

    public void orderPayment(OrderCriteria.OrderPayment criteria) {
        userService.getUser(criteria.getUserId());

        ProductInfo.OrderProducts orderProducts = productService.getOrderProducts(criteria.toProductCommand());

        /* week3 adv : 쿠폰 조회 및 적용 추가 */
        Optional<Long> optionalCouponId = Optional.ofNullable(criteria.getUserCouponId());
        Optional<UserCouponInfo.UsableCoupon> optionalUserCoupon = optionalCouponId.map(id -> userCouponService.getUsableCoupon(criteria.toCouponCommand()));
        Optional<CouponInfo.Coupon> optionalCoupon = optionalCouponId.map(couponService::getCoupon);

        OrderCommand.Create orderCommand = criteria.toOrderCommand(
            orderProducts,
            optionalUserCoupon.map(UserCouponInfo.UsableCoupon::getUserCouponId).orElse(null),
            optionalCoupon.map(CouponInfo.Coupon::getDiscountRate).orElse(NOT_DISCOUNT)
        );
        OrderInfo.Order order = orderService.createOrder(orderCommand);

        balanceService.useBalance(criteria.toBalanceCommand(order.getTotalPrice()));
        optionalUserCoupon.ifPresent(userCoupon -> userCouponService.useUserCoupon(userCoupon.getUserCouponId()));
        stockService.deductStock(criteria.toStockCommand());
        paymentService.pay(criteria.toPaymentCommand(order));
        orderService.paidOrder(order.getOrderId());
    }
}
