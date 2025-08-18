package kr.hhplus.be.server.application.product;

import java.time.LocalDate;

import kr.hhplus.be.server.domain.stock.StockService;
import kr.hhplus.be.server.domain.stock.StockInfo;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.product.ProductInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ProductFacade {

    private final ProductService productService;
    private final StockService stockService;

    @Transactional(readOnly = true)
    public ProductResult.Products getProducts() {
        ProductInfo.Products products = productService.getSellingProducts();
        return ProductResult.Products.of(products.getProducts().stream()
            .map(this::getProduct)
            .toList());
    }

    private ProductResult.Product getProduct(ProductInfo.Product product) {
        StockInfo.Stock stock = stockService.getStock(product.getProductId());

        return ProductResult.Product.builder()
            .productId(product.getProductId())
            .productName(product.getProductName())
            .productPrice(product.getProductPrice())
            .quantity(stock.getQuantity())
            .build();
    }
} 