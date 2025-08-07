package kr.hhplus.be.server.infrastructure.stock;

import kr.hhplus.be.server.domain.stock.*;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StockRepositoryImpl implements StockRepository {

    private final StockJpaRepository stockJpaRepository;

    @Override
    public Stock save(Stock stock) {
        return stockJpaRepository.save(stock);
    }

    @Override
    public Stock findByProductId(Long productId) {
        Stock stock = stockJpaRepository.findByProductId(productId);
        if (stock == null) {
            throw new IllegalArgumentException("재고가 없습니다.");
        }
        return stock;
    }

    @Override
    public Stock findWithLockByProductId(Long productId) {
        return stockJpaRepository.findWithLockByProductId(productId)
            .orElseThrow(() -> new IllegalArgumentException("재고가 없습니다."));
    }
} 