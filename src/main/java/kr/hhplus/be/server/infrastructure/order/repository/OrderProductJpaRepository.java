package kr.hhplus.be.server.infrastructure.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.domain.order.OrderProduct;

public interface OrderProductJpaRepository extends JpaRepository<OrderProduct, Long> {

    List<OrderProduct> findByOrderIdIn(List<Long> orderIds);
}
