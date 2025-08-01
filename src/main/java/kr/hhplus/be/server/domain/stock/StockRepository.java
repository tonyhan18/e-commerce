package kr.hhplus.be.server.domain.stock;

import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository {

    Stock save(Stock stock);
    Stock findByProductId(Long productId);
}