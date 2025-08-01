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
            .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));
    }

    @Override
    public List<OrderProduct> findOrderIdsIn(List<Long> orderIds) {
        return orderProductJpaRepository.findByOrderIdIn(orderIds);
    }

    @Override
    public void sendOrderMessage(Order order) {
        // 주문 메시지 전송 로직은 별도 서비스에서 처리
        // 여기서는 기본 구현만 제공
    }
} 