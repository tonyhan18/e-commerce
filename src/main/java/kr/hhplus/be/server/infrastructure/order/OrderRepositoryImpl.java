package kr.hhplus.be.server.infrastructure.order;

import kr.hhplus.be.server.domain.order.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
    
    private final OrderJpaRepository orderJpaRepository;
    private final OrderProductJpaRepository orderProductJpaRepository;

    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }

    @Override
    public Order findById(Long id) {
        return orderJpaRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException("주문이 존재하지 않습니다. : " + id));
    }

    @Override
    public List<OrderProduct> findOrderIdsIn(List<Long> orderIds) {
        return orderProductJpaRepository.findByOrderIdIn(orderIds);
    }
} 