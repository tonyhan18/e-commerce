package kr.hhplus.be.server.domain.order;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    private OrderProduct createOrderProduct(OrderCommand.OrderProduct command) {
        return OrderProduct.create(
            command.getProductId(), 
            command.getProductName(), 
            command.getProductPrice(), 
            command.getQuantity()
        );
    }

    private static List<Long> sortedProducts(Map<Long, Integer> productQuantityMap) {
        return productQuantityMap.entrySet().stream()
            .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

    private Map<Long, Integer> getProductQuantityMap(List<OrderProduct> orderProducts) {
        return orderProducts.stream()
            .collect(Collectors.groupingBy(OrderProduct::getProductId, Collectors.summingInt(OrderProduct::getQuantity)));
    }

    public OrderInfo.Order createOrder(OrderCommand.Create command) {
        List<OrderProduct> orderProducts = command.getProducts().stream()
            .map(this::createOrderProduct)
            .collect(Collectors.toList());

        Order order = Order.create(
            command.getUserId(), 
            orderProducts
        );
        orderRepository.save(order);

        return OrderInfo.Order.of(order.getId(), order.getTotalPrice());
    }

    public OrderInfo.TopPaidProducts getTopPaidProducts(OrderCommand.TopOrders command) {
        List<OrderProduct> orderProducts = orderRepository.findOrderIdsIn(command.getOrderIds());

        Map<Long, Integer> productQuantityMap = getProductQuantityMap(orderProducts);
        List<Long> sortedProductIds = sortedProducts(productQuantityMap);

        return OrderInfo.TopPaidProducts.of(sortedProductIds);
    }

    public void paidOrder(Long orderId) {
        Order order = orderRepository.findById(orderId);
        order.paid();
        orderRepository.sendOrderMessage(order);
    }
}
