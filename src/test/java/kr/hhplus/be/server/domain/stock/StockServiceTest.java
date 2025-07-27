package kr.hhplus.be.server.domain.stock;

import kr.hhplus.be.server.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StockServiceTest extends MockTestSupport {

    @InjectMocks
    private StockService stockService;

    @Mock
    private StockRepository stockRepository;

    @Test
    @DisplayName("재고 정보를 조회할 수 있다.")
    void getStock() {
        // given
        Long productId = 1L;
        int quantity = 100;

        Stock stock = Stock.builder()
            .id(1L)
            .productId(productId)
            .quantity(quantity)
            .build();

        when(stockRepository.findByProductId(productId)).thenReturn(stock);

        // when
        StockInfo.Stock result = stockService.getStock(productId);

        // then
        assertThat(result.getProductId()).isEqualTo(productId);
        assertThat(result.getQuantity()).isEqualTo(quantity);
    }

    @Test
    @DisplayName("재고가 없으면 예외가 발생한다.")
    void getStockWhenEmpty() {
        // given
        Long productId = 1L;

        Stock stock = Stock.builder()
            .id(1L)
            .productId(productId)
            .quantity(0)
            .build();

        when(stockRepository.findByProductId(productId)).thenReturn(stock);

        // when & then
        assertThatThrownBy(() -> stockService.getStock(productId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("재고가 없습니다.");
    }

    @Test
    @DisplayName("재고를 차감할 수 있다.")
    void deductStock() {
        // given
        Long productId1 = 1L;
        Long productId2 = 2L;
        int quantity1 = 10;
        int quantity2 = 20;

        Stock stock1 = Stock.builder()
            .id(1L)
            .productId(productId1)
            .quantity(100)
            .build();

        Stock stock2 = Stock.builder()
            .id(2L)
            .productId(productId2)
            .quantity(200)
            .build();

        StockCommand.OrderProduct orderProduct1 = StockCommand.OrderProduct.of(productId1, quantity1);
        StockCommand.OrderProduct orderProduct2 = StockCommand.OrderProduct.of(productId2, quantity2);
        StockCommand.OrderProducts orderProducts = StockCommand.OrderProducts.of(List.of(orderProduct1, orderProduct2));

        when(stockRepository.findByProductId(productId1)).thenReturn(stock1);
        when(stockRepository.findByProductId(productId2)).thenReturn(stock2);

        // when
        stockService.deductStock(orderProducts);

        // then
        assertThat(stock1.getQuantity()).isEqualTo(90); // 100 - 10
        assertThat(stock2.getQuantity()).isEqualTo(180); // 200 - 20
        verify(stockRepository, times(1)).findByProductId(productId1);
        verify(stockRepository, times(1)).findByProductId(productId2);
    }

    @Test
    @DisplayName("재고를 추가할 수 있다.")
    void addStock() {
        // given
        Long productId1 = 1L;
        Long productId2 = 2L;
        int quantity1 = 10;
        int quantity2 = 20;

        Stock stock1 = Stock.builder()
            .id(1L)
            .productId(productId1)
            .quantity(100)
            .build();

        Stock stock2 = Stock.builder()
            .id(2L)
            .productId(productId2)
            .quantity(200)
            .build();

        StockCommand.OrderProduct orderProduct1 = StockCommand.OrderProduct.of(productId1, quantity1);
        StockCommand.OrderProduct orderProduct2 = StockCommand.OrderProduct.of(productId2, quantity2);
        StockCommand.OrderProducts orderProducts = StockCommand.OrderProducts.of(List.of(orderProduct1, orderProduct2));

        when(stockRepository.findByProductId(productId1)).thenReturn(stock1);
        when(stockRepository.findByProductId(productId2)).thenReturn(stock2);

        // when
        stockService.addStock(orderProducts);

        // then
        assertThat(stock1.getQuantity()).isEqualTo(110); // 100 + 10
        assertThat(stock2.getQuantity()).isEqualTo(220); // 200 + 20
        verify(stockRepository, times(1)).findByProductId(productId1);
        verify(stockRepository, times(1)).findByProductId(productId2);
    }

    @Test
    @DisplayName("단일 상품의 재고를 차감할 수 있다.")
    void deductStockSingleProduct() {
        // given
        Long productId = 1L;
        int quantity = 50;

        Stock stock = Stock.builder()
            .id(1L)
            .productId(productId)
            .quantity(100)
            .build();

        StockCommand.OrderProduct orderProduct = StockCommand.OrderProduct.of(productId, quantity);
        StockCommand.OrderProducts orderProducts = StockCommand.OrderProducts.of(List.of(orderProduct));

        when(stockRepository.findByProductId(productId)).thenReturn(stock);

        // when
        stockService.deductStock(orderProducts);

        // then
        assertThat(stock.getQuantity()).isEqualTo(50); // 100 - 50
        verify(stockRepository, times(1)).findByProductId(productId);
    }

    @Test
    @DisplayName("단일 상품의 재고를 추가할 수 있다.")
    void addStockSingleProduct() {
        // given
        Long productId = 1L;
        int quantity = 50;

        Stock stock = Stock.builder()
            .id(1L)
            .productId(productId)
            .quantity(100)
            .build();

        StockCommand.OrderProduct orderProduct = StockCommand.OrderProduct.of(productId, quantity);
        StockCommand.OrderProducts orderProducts = StockCommand.OrderProducts.of(List.of(orderProduct));

        when(stockRepository.findByProductId(productId)).thenReturn(stock);

        // when
        stockService.addStock(orderProducts);

        // then
        assertThat(stock.getQuantity()).isEqualTo(150); // 100 + 50
        verify(stockRepository, times(1)).findByProductId(productId);
    }

    @Test
    @DisplayName("빈 주문 상품 목록으로 재고를 차감할 수 있다.")
    void deductStockWithEmptyOrderProducts() {
        // given
        StockCommand.OrderProducts orderProducts = StockCommand.OrderProducts.of(List.of());

        // when
        stockService.deductStock(orderProducts);

        // then
        verify(stockRepository, times(0)).findByProductId(any());
    }

    @Test
    @DisplayName("빈 주문 상품 목록으로 재고를 추가할 수 있다.")
    void addStockWithEmptyOrderProducts() {
        // given
        StockCommand.OrderProducts orderProducts = StockCommand.OrderProducts.of(List.of());

        // when
        stockService.addStock(orderProducts);

        // then
        verify(stockRepository, times(0)).findByProductId(any());
    }

    @Test
    @DisplayName("재고가 0인 상태에서 재고 정보를 조회하면 예외가 발생한다.")
    void getStockWithZeroQuantity() {
        // given
        Long productId = 1L;

        Stock stock = Stock.builder()
            .id(1L)
            .productId(productId)
            .quantity(0)
            .build();

        when(stockRepository.findByProductId(productId)).thenReturn(stock);

        // when & then
        assertThatThrownBy(() -> stockService.getStock(productId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("재고가 없습니다.");
    }

    @Test
    @DisplayName("재고가 1인 상태에서 재고 정보를 조회할 수 있다.")
    void getStockWithOneQuantity() {
        // given
        Long productId = 1L;

        Stock stock = Stock.builder()
            .id(1L)
            .productId(productId)
            .quantity(1)
            .build();

        when(stockRepository.findByProductId(productId)).thenReturn(stock);

        // when
        StockInfo.Stock result = stockService.getStock(productId);

        // then
        assertThat(result.getProductId()).isEqualTo(productId);
        assertThat(result.getQuantity()).isEqualTo(1);
    }

    @Test
    @DisplayName("여러 상품의 재고를 연속으로 차감할 수 있다.")
    void deductStockMultipleProducts() {
        // given
        Long productId1 = 1L;
        Long productId2 = 2L;
        Long productId3 = 3L;

        Stock stock1 = Stock.builder()
            .id(1L)
            .productId(productId1)
            .quantity(100)
            .build();

        Stock stock2 = Stock.builder()
            .id(2L)
            .productId(productId2)
            .quantity(200)
            .build();

        Stock stock3 = Stock.builder()
            .id(3L)
            .productId(productId3)
            .quantity(300)
            .build();

        StockCommand.OrderProduct orderProduct1 = StockCommand.OrderProduct.of(productId1, 10);
        StockCommand.OrderProduct orderProduct2 = StockCommand.OrderProduct.of(productId2, 20);
        StockCommand.OrderProduct orderProduct3 = StockCommand.OrderProduct.of(productId3, 30);
        StockCommand.OrderProducts orderProducts = StockCommand.OrderProducts.of(List.of(orderProduct1, orderProduct2, orderProduct3));

        when(stockRepository.findByProductId(productId1)).thenReturn(stock1);
        when(stockRepository.findByProductId(productId2)).thenReturn(stock2);
        when(stockRepository.findByProductId(productId3)).thenReturn(stock3);

        // when
        stockService.deductStock(orderProducts);

        // then
        assertThat(stock1.getQuantity()).isEqualTo(90);  // 100 - 10
        assertThat(stock2.getQuantity()).isEqualTo(180); // 200 - 20
        assertThat(stock3.getQuantity()).isEqualTo(270); // 300 - 30
        verify(stockRepository, times(1)).findByProductId(productId1);
        verify(stockRepository, times(1)).findByProductId(productId2);
        verify(stockRepository, times(1)).findByProductId(productId3);
    }

    @Test
    @DisplayName("여러 상품의 재고를 연속으로 추가할 수 있다.")
    void addStockMultipleProducts() {
        // given
        Long productId1 = 1L;
        Long productId2 = 2L;
        Long productId3 = 3L;

        Stock stock1 = Stock.builder()
            .id(1L)
            .productId(productId1)
            .quantity(100)
            .build();

        Stock stock2 = Stock.builder()
            .id(2L)
            .productId(productId2)
            .quantity(200)
            .build();

        Stock stock3 = Stock.builder()
            .id(3L)
            .productId(productId3)
            .quantity(300)
            .build();

        StockCommand.OrderProduct orderProduct1 = StockCommand.OrderProduct.of(productId1, 10);
        StockCommand.OrderProduct orderProduct2 = StockCommand.OrderProduct.of(productId2, 20);
        StockCommand.OrderProduct orderProduct3 = StockCommand.OrderProduct.of(productId3, 30);
        StockCommand.OrderProducts orderProducts = StockCommand.OrderProducts.of(List.of(orderProduct1, orderProduct2, orderProduct3));

        when(stockRepository.findByProductId(productId1)).thenReturn(stock1);
        when(stockRepository.findByProductId(productId2)).thenReturn(stock2);
        when(stockRepository.findByProductId(productId3)).thenReturn(stock3);

        // when
        stockService.addStock(orderProducts);

        // then
        assertThat(stock1.getQuantity()).isEqualTo(110); // 100 + 10
        assertThat(stock2.getQuantity()).isEqualTo(220); // 200 + 20
        assertThat(stock3.getQuantity()).isEqualTo(330); // 300 + 30
        verify(stockRepository, times(1)).findByProductId(productId1);
        verify(stockRepository, times(1)).findByProductId(productId2);
        verify(stockRepository, times(1)).findByProductId(productId3);
    }
} 