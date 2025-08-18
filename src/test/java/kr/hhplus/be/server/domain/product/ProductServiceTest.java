package kr.hhplus.be.server.domain.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
            .id(1L)
            .name("테스트 상품")
            .price(10000L)
            .sellStatus(ProductSellingStatus.SELLING)
            .build();
    }

    @Test
    @DisplayName("주문 상품 조회 - 성공")
    void getOrderProducts_success() {
        // given
        List<ProductCommand.OrderProduct> orderProducts = List.of(
            ProductCommand.OrderProduct.of(1L, 2),
            ProductCommand.OrderProduct.of(2L, 1)
        );
        ProductCommand.OrderProducts command = ProductCommand.OrderProducts.of(orderProducts);
        
        when(productRepository.findById(1L)).thenReturn(testProduct);
        when(productRepository.findById(2L)).thenReturn(testProduct);

        // when
        ProductInfo.OrderProducts result = productService.getOrderProducts(command);

        // then
        assertThat(result.getProducts()).hasSize(2);
        verify(productRepository, times(2)).findById(any(Long.class));
    }

    @Test
    @DisplayName("주문 상품 조회 - 주문 불가능한 상품 포함")
    void getOrderProducts_cannotSelling() {
        // given
        Product unsellableProduct = Product.builder()
            .id(1L)
            .name("판매 중단 상품")
            .price(10000L)
            .sellStatus(ProductSellingStatus.STOP_SELLING)
            .build();
        
        List<ProductCommand.OrderProduct> orderProducts = List.of(
            ProductCommand.OrderProduct.of(1L, 2)
        );
        ProductCommand.OrderProducts command = ProductCommand.OrderProducts.of(orderProducts);
        
        when(productRepository.findById(1L)).thenReturn(unsellableProduct);

        // when & then
        assertThatThrownBy(() -> productService.getOrderProducts(command))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("주문 불가한 상품이 포함되어 있습니다.");
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("판매 중인 상품 목록 조회 - 성공")
    void getSellingProducts_success() {
        // given
        List<Product> products = List.of(testProduct);
        when(productRepository.findSellingStatusIn(ProductSellingStatus.forSelling()))
            .thenReturn(products);

        // when
        ProductInfo.Products result = productService.getSellingProducts();

        // then
        assertThat(result.getProducts()).hasSize(1);
        verify(productRepository, times(1)).findSellingStatusIn(ProductSellingStatus.forSelling());
    }

    @Test
    @DisplayName("상품 목록 조회 - 성공")
    void getProducts_success() {
        // given
        List<Long> productIds = List.of(1L, 2L);
        ProductCommand.Products command = ProductCommand.Products.of(productIds);
        
        when(productRepository.findById(1L)).thenReturn(testProduct);
        when(productRepository.findById(2L)).thenReturn(testProduct);

        // when
        ProductInfo.Products result = productService.getProducts(command);

        // then
        assertThat(result.getProducts()).hasSize(2);
        verify(productRepository, times(2)).findById(any(Long.class));
    }
} 