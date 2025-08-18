package kr.hhplus.be.server.application.product;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.rank.Rank;
import kr.hhplus.be.server.domain.rank.RankRepository;
import kr.hhplus.be.server.domain.stock.Stock;
import kr.hhplus.be.server.domain.stock.StockRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRespository;
import kr.hhplus.be.server.application.product.ProductResult;
import kr.hhplus.be.server.domain.product.ProductSellingStatus;
import kr.hhplus.be.server.support.IntegrationTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
class ProductFacadeIntegrationTest extends IntegrationTestSupport{

    @Autowired
    private ProductFacade productFacade;

    @Autowired
    private ProductRespository productRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private RankRepository rankRepository;

    private Product product1;

    private Product product2;

    private Product product3;

    @BeforeEach
    void setUp() {
        product1 = Product.create("상품명1", 1_000L, ProductSellingStatus.SELLING);
        product2 = Product.create("상품명2", 2_000L, ProductSellingStatus.SELLING);
        product3 = Product.create("상품명3", 3_000L, ProductSellingStatus.STOP_SELLING);

        List.of(product1, product2, product3)
            .forEach(productRepository::save);

        Stock stock1 = Stock.create(product1.getId(), 10);
        Stock stock2 = Stock.create(product2.getId(), 20);
        Stock stock3 = Stock.create(product3.getId(), 30);

        List.of(stock1, stock2, stock3)
            .forEach(stockRepository::save);

    }

    @DisplayName("판매 가능 상품 목록을 조회한다.")
    @Test
    void getProducts() {
        // when
        ProductResult.Products products = productFacade.getProducts();

        // then
        assertThat(products.getProducts()).hasSize(2)
            .extracting(ProductResult.Product::getProductId)
            .containsExactlyInAnyOrder(product1.getId(), product2.getId());
    }

    // @DisplayName("상위 상품을 조회한다.")
    // @Test
    // void getPopularProducts() {
    //     // given
    //     List<Rank> ranks = List.of(
    //         Rank.createSell(product1.getId(), LocalDate.of(2025, 4, 23), 10L),
    //         Rank.createSell(product2.getId(), LocalDate.of(2025, 4, 22), 34L),
    //         Rank.createSell(product3.getId(), LocalDate.of(2025, 4, 23), 42L)
    //     );

    //     ranks.forEach(rankRepository::save);

    //     // when
    //     ProductResult.Products products = productFacade.getPopularProducts();

    //     // then
    //     assertThat(products.getProducts()).hasSize(3)
    //         .extracting("productId")
    //         .containsExactly(product3.getId(), product2.getId(), product1.getId());
    // }
} 