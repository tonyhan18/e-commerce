package kr.hhplus.be.server.domain.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryTest {

    @Mock
    private ProductRepository productRepository;

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
    @DisplayName("상품 저장 - 성공")
    void save_success() {
        // given
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // when
        Product savedProduct = productRepository.save(testProduct);

        // then
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isEqualTo(1L);
        verify(productRepository, times(1)).save(testProduct);
    }

    @Test
    @DisplayName("상품 ID로 조회 - 성공")
    void findById_success() {
        // given
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(testProduct);

        // when
        Product result = productRepository.findById(productId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("테스트 상품");
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("상품 ID로 조회 - 존재하지 않는 상품")
    void findById_notFound() {
        // given
        Long productId = 999L;
        when(productRepository.findById(productId))
            .thenThrow(new IllegalArgumentException("Product not found with id: 999"));

        // when & then
        assertThat(productRepository.findById(productId))
            .isNull();
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("판매 상태로 상품 목록 조회 - 성공")
    void findSellingStatusIn_success() {
        // given
        List<ProductSellingStatus> sellStatuses = List.of(ProductSellingStatus.SELLING);
        List<Product> products = List.of(testProduct);
        when(productRepository.findSellingStatusIn(sellStatuses)).thenReturn(products);

        // when
        List<Product> result = productRepository.findSellingStatusIn(sellStatuses);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        verify(productRepository, times(1)).findSellingStatusIn(sellStatuses);
    }
} 