package kr.hhplus.be.server.infrastructure.stock;

import kr.hhplus.be.server.domain.stock.*;
import org.springframework.stereotype.Component;

@Component
public class StockRepositoryImpl implements StockRepository {
    @Override
    public Stock findByProductId(Long productId) {
        // TODO: 실제 DB 연동 로직 구현
        return null;
    }
} 