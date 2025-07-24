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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderFacade {

    private final UserService userService;
    private final ProductService productService;
    private final OrderService orderService;
    private final BalanceService balanceService;
    private final StockService stockService;
    private final PaymentService paymentService;

    public void orderPayment(OrderCriteria.OrderPayment criteria) {
        userService.getUser(criteria.getUserId());

        ProductInfo.OrderProducts orderProducts = productService.getOrderProducts(criteria.toProductCommand());

        OrderCommand.Create orderCommand = criteria.toOrderCommand(orderProducts);
        OrderInfo.Order order = orderService.createOrder(orderCommand);

        balanceService.useBalance(criteria.toBalanceCommand(order.getTotalPrice()));
        
        stockService.deductStock(criteria.toStockCommand());
        paymentService.pay(criteria.toPaymentCommand(order));
        orderService.paidOrder(order.getOrderId());
    }
}
