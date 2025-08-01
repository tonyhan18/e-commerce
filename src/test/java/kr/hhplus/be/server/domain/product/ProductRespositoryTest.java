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
class ProductRespositoryTest {

    @Mock
    private ProductRespository productRespository;

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
        when(productRespository.save(any(Product.class))).thenReturn(testProduct);

        // when
        Product savedProduct = productRespository.save(testProduct);

        // then
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isEqualTo(1L);
        verify(productRespository, times(1)).save(testProduct);
    }

    @Test
    @DisplayName("상품 ID로 조회 - 성공")
    void findById_success() {
        // given
        Long productId = 1L;
        when(productRespository.findById(productId)).thenReturn(testProduct);

        // when
        Product result = productRespository.findById(productId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("테스트 상품");
        verify(productRespository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("상품 ID로 조회 - 존재하지 않는 상품")
    void findById_notFound() {
        // given
        Long productId = 999L;
        when(productRespository.findById(productId))
            .thenThrow(new IllegalArgumentException("Product not found with id: 999"));

        // when & then
        assertThat(productRespository.findById(productId))
            .isNull();
        verify(productRespository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("판매 상태로 상품 목록 조회 - 성공")
    void findSellingStatusIn_success() {
        // given
        List<ProductSellingStatus> sellStatuses = List.of(ProductSellingStatus.SELLING);
        List<Product> products = List.of(testProduct);
        when(productRespository.findSellingStatusIn(sellStatuses)).thenReturn(products);

        // when
        List<Product> result = productRespository.findSellingStatusIn(sellStatuses);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        verify(productRespository, times(1)).findSellingStatusIn(sellStatuses);
    }
} 