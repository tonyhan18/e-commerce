package kr.hhplus.be.server.infrastructure.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.domain.order.Order;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {

}
