package kr.hhplus.be.server.application.product;

import java.time.LocalDate;

import kr.hhplus.be.server.domain.stock.StockService;
import kr.hhplus.be.server.domain.stock.StockInfo;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.product.ProductInfo;
import kr.hhplus.be.server.domain.product.ProductCommand;
import kr.hhplus.be.server.domain.rank.RankCommand;
import kr.hhplus.be.server.domain.rank.RankInfo;
import kr.hhplus.be.server.domain.rank.RankService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ProductFacade {
    private static final int RECENT_DAYS = 3;
    private static final int TOP_LIMIT = 5;

    private final ProductService productService;
    private final StockService stockService;
    private final RankService rankService;

    @Transactional(readOnly = true)
    public ProductResult.Products getProducts() {
        ProductInfo.Products products = productService.getSellingProducts();
        return ProductResult.Products.of(products.getProducts().stream()
            .map(this::getProduct)
            .toList());
    }

    @Transactional(readOnly = true)
    public ProductResult.Products getPopularProducts() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(RECENT_DAYS);

        RankCommand.PopularSellRank popularSellRankCommand = RankCommand.PopularSellRank.of(TOP_LIMIT, startDate, endDate);
        RankInfo.PopularProducts popularProducts = rankService.getPopularSellRank(popularSellRankCommand);

        ProductCommand.Products productsCommand = ProductCommand.Products.of(popularProducts.getProductIds());
        ProductInfo.Products products = productService.getProducts(productsCommand);

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