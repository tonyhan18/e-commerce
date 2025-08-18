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
    private final OrderQueryDslRepository orderQueryDslRepository;  

    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }

    @Override
    public Order findById(Long id) {
        return orderJpaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다."));
    }

    @Override
    public List<OrderProduct> findOrderIdsIn(List<Long> orderIds) {
        return orderProductJpaRepository.findByOrderIdIn(orderIds);
    }

    @Override
    public List<OrderInfo.PaidProduct> findPaidProducts(OrderCommand.PaidProducts command) {
        return orderQueryDslRepository.findPaidProducts(command);
    }

    @Override
    public void sendOrderMessage(Order order) {
        // TODO: 메시지 전송 로직 구현
        // 현재는 구현하지 않음
    }
} 