package kr.hhplus.be.server.infrastructure.order;

import kr.hhplus.be.server.domain.order.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderRepositoryImpl implements OrderRepository {
    @Override
    public Order save(Order order) {
        // TODO: 실제 DB 연동 로직 구현
        return order;
    }

    @Override
    public Order findById(Long id) {
        // TODO: 실제 DB 연동 로직 구현
        return null;
    }

    @Override
    public void sendOrderMessage(Order order) {
        // TODO: 메시지 큐 연동 등 구현
    }

    @Override
    public List<OrderProduct> findOrderIdsIn(List<Long> orderIds) {
        // TODO: 실제 DB 연동 로직 구현
        return null;
    }
} 