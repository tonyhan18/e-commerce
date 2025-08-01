package kr.hhplus.be.server.domain.stock;

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
class StockServiceIntegrationTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private Stock testStock;

    @InjectMocks
    private StockService stockService;


    @Test
    @DisplayName("재고 조회 - 성공")
    void getStock_success() {
        // given
        Long productId = 1L;
        when(testStock.getProductId()).thenReturn(1L);
        when(testStock.getQuantity()).thenReturn(100);
        when(testStock.isEmpty()).thenReturn(false);
        when(stockRepository.findByProductId(productId)).thenReturn(testStock);

        // when
        StockInfo.Stock result = stockService.getStock(productId);

        // then
        assertThat(result).isNotNull();
        verify(stockRepository, times(1)).findByProductId(productId);
    }

    @Test
    @DisplayName("재고 조회 - 재고 없음")
    void getStock_empty() {
        // given
        Long productId = 1L;
        Stock emptyStock = mock(Stock.class);
        when(emptyStock.isEmpty()).thenReturn(true);
        when(stockRepository.findByProductId(productId)).thenReturn(emptyStock);

        // when & then
        assertThatThrownBy(() -> stockService.getStock(productId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("재고가 없습니다.");
        verify(stockRepository, times(1)).findByProductId(productId);
    }

    @Test
    @DisplayName("재고 차감 - 성공")
    void deductStock_success() {
        // given
        List<StockCommand.OrderProduct> orderProducts = List.of(
            StockCommand.OrderProduct.of(1L, 2),
            StockCommand.OrderProduct.of(2L, 1)
        );
        StockCommand.OrderProducts command = StockCommand.OrderProducts.of(orderProducts);
        when(stockRepository.findByProductId(1L)).thenReturn(testStock);
        when(stockRepository.findByProductId(2L)).thenReturn(testStock);

        // when
        stockService.deductStock(command);

        // then
        verify(stockRepository, times(2)).findByProductId(any(Long.class));
        verify(testStock, times(2)).deductQuantity(any(Integer.class));
    }

    @Test
    @DisplayName("재고 추가 - 성공")
    void addStock_success() {
        // given
        List<StockCommand.OrderProduct> orderProducts = List.of(
            StockCommand.OrderProduct.of(1L, 2),
            StockCommand.OrderProduct.of(2L, 1)
        );
        StockCommand.OrderProducts command = StockCommand.OrderProducts.of(orderProducts);
        when(stockRepository.findByProductId(1L)).thenReturn(testStock);
        when(stockRepository.findByProductId(2L)).thenReturn(testStock);

        // when
        stockService.addStock(command);

        // then
        verify(stockRepository, times(2)).findByProductId(any(Long.class));
        verify(testStock, times(2)).addQuantity(any(Integer.class));
    }
} 