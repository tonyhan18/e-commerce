package kr.hhplus.be.server.domain.order;

import java.util.List;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderExternalClient orderExternalClient;


    public OrderInfo.Order createOrder(OrderCommand.Create command) {
        List<OrderProduct> orderProducts = command.getProducts().stream()
            .map(this::createOrderProduct)
            .collect(Collectors.toList());

        Order order = Order.create(
            command.getUserId(), 
            orderProducts,
            command.getUserCouponId(),
            command.getDiscountRate()
        );
        orderRepository.save(order);

        return OrderInfo.Order.of(order.getId(), order.getTotalPrice(), order.getDiscountPrice());
    }

    public void paidOrder(Long orderId) {
        Order order = orderRepository.findById(orderId);
        order.paid(LocalDateTime.now());
        orderExternalClient.sendOrderMessage(order);
    }

    public OrderInfo.PaidProducts getPaidProducts(OrderCommand.PaidProducts command) {
        OrderCommand.PaidProducts queryCommand = command.toPaidProductsQuery(OrderStatus.PAID);
        List<OrderInfo.PaidProduct> paidProducts = orderRepository.findPaidProducts(queryCommand);
        return OrderInfo.PaidProducts.of(paidProducts);
    }

    private OrderProduct createOrderProduct(OrderCommand.OrderProduct command) {
        return OrderProduct.create(
            command.getProductId(), 
            command.getProductName(), 
            command.getProductPrice(), 
            command.getQuantity()
        );
    }
}
