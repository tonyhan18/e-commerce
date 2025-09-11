package kr.hhplus.be.server.domain.product;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository {

    Product save(Product product);
    Product findById(Long productId);
    List<ProductInfo.Product> findSellingStatusIn(List<ProductSellingStatus> sellStatuses);
    List<ProductInfo.Product> findAll(ProductCommand.Query command);
}