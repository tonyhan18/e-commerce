package kr.hhplus.be.server.infrastructure.stock.repository;

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
        return stockJpaRepository.findByProductId(productId)
        .orElseThrow(() -> new IllegalArgumentException("재고가 존재하지 않습니다."));
    }

    @Override
    public Stock findByProductIdWithLock(Long productId) {
        return stockJpaRepository.findByProductIdWithLock(productId)
        .orElseThrow(() -> new IllegalArgumentException("재고가 존재하지 않습니다."));
    }
} 