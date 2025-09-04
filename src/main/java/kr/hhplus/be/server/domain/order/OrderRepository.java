package kr.hhplus.be.server.domain.order;

import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository {
    Order save(Order order);

    Order findById(Long id);
}
