package kr.hhplus.be.server.domain.stock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockRepositoryTest {

    @Mock
    private StockRepository stockRepository;

    private Stock testStock;

    @BeforeEach
    void setUp() {
        testStock = Stock.create(1L, 100);
    }

    @Test
    @DisplayName("재고 저장 - 성공")
    void save_success() {
        // given
        when(stockRepository.save(any(Stock.class))).thenReturn(testStock);

        // when
        Stock savedStock = stockRepository.save(testStock);

        // then
        assertThat(savedStock).isNotNull();
        verify(stockRepository, times(1)).save(testStock);
    }

    @Test
    @DisplayName("상품 ID로 재고 조회 - 성공")
    void findByProductId_success() {
        // given
        Long productId = 1L;
        when(stockRepository.findByProductId(productId)).thenReturn(testStock);

        // when
        Stock result = stockRepository.findByProductId(productId);

        // then
        assertThat(result).isNotNull();
        verify(stockRepository, times(1)).findByProductId(productId);
    }

    @Test
    @DisplayName("상품 ID로 재고 조회 - 존재하지 않는 상품")
    void findByProductId_notFound() {
        // given
        Long productId = 999L;
        when(stockRepository.findByProductId(productId))
            .thenReturn(null);

        // when
        Stock result = stockRepository.findByProductId(productId);

        // then
        assertThat(result).isNull();
        verify(stockRepository, times(1)).findByProductId(productId);
    }
} 