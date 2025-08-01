package kr.hhplus.be.server.infrastructure.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductSellingStatus;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {
    List<Product> findBySellStatusIn(List<ProductSellingStatus> sellStatuses);
    List<Product> findByIdIn(List<Long> productIds);
}
