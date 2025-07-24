package kr.hhplus.be.server.domain.product;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface ProductRespository {

    Product findById(Long productId);
    List<Product> findSellingStatusIn(List<ProductSellingStatus> sellStatuses);

    List<Product> findByIds(List<Long> productIds);
}