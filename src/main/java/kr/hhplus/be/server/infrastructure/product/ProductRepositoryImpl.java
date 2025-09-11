package kr.hhplus.be.server.infrastructure.product;

import kr.hhplus.be.server.domain.product.*;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;

    private final ProductQueryDslRepository productQueryDslRepository;
    @Override
    public Product save(Product product) {
        return productJpaRepository.save(product);
    }

    @Override
    public Product findById(Long productId) {
        return productJpaRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));
    }

    @Override
    public List<ProductInfo.Product> findSellingStatusIn(List<ProductSellingStatus> sellStatuses) {
        return productQueryDslRepository.findBySellStatusIn(sellStatuses);
    }

    @Override
    public List<ProductInfo.Product> findAll(ProductCommand.Query command) {
        return productQueryDslRepository.findAll(command);
    }
} 