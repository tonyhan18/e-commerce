package kr.hhplus.be.server.infrastructure.stock;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.domain.stock.Stock;

public interface StockJpaRepository extends JpaRepository<Stock, Long> {
    Stock findByProductId(Long productId);
}
