package kr.hhplus.be.server.domain.order;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository {
    Order save(Order order);

    Order findById(Long id);

    List<OrderProduct> findOrderIdsIn(List<Long> orderIds);

    // List<OrderInfo.PaidProduct> findPaidProducts(OrderCommand.PaidProducts command);
}
