package kr.hhplus.be.server.infrastructure.product;

import kr.hhplus.be.server.domain.product.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductRespositoryImpl implements ProductRespository {
    @Override
    public Product findById(Long productId) {
        // TODO: 실제 DB 연동 로직 구현
        return null;
    }

    @Override
    public List<Product> findSellingStatusIn(List<ProductSellingStatus> sellStatuses) {
        // TODO: 실제 DB 연동 로직 구현
        return List.of();
    }

    @Override
    public List<Product> findByIds(List<Long> productIds) {
        // TODO: 실제 DB 연동 로직 구현
        return List.of();
    }
} 