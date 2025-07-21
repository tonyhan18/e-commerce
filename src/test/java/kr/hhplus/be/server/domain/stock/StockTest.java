package kr.hhplus.be.server.domain.stock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StockTest {

    @Test
    @DisplayName("재고를 생성할 수 있다.")
    void createStock() {
        // given
        Long productId = 1L;
        int quantity = 100;

        // when
        Stock stock = Stock.create(productId, quantity);

        // then
        assertThat(stock.getProductId()).isEqualTo(productId);
        assertThat(stock.getQuantity()).isEqualTo(quantity);
        assertThat(stock.getId()).isNull(); // ID는 저장 시점에 생성됨
    }

    @Test
    @DisplayName("빌더 패턴으로 재고를 생성할 수 있다.")
    void createStockWithBuilder() {
        // given
        Long stockId = 1L;
        Long productId = 2L;
        int quantity = 50;

        // when
        Stock stock = Stock.builder()
            .id(stockId)
            .productId(productId)
            .quantity(quantity)
            .build();

        // then
        assertThat(stock.getId()).isEqualTo(stockId);
        assertThat(stock.getProductId()).isEqualTo(productId);
        assertThat(stock.getQuantity()).isEqualTo(quantity);
    }

    @Test
    @DisplayName("재고를 차감할 수 있다.")
    void deductQuantity() {
        // given
        Stock stock = Stock.create(1L, 100);
        int deductQuantity = 30;

        // when
        stock.deductQuantity(deductQuantity);

        // then
        assertThat(stock.getQuantity()).isEqualTo(70);
    }

    @Test
    @DisplayName("재고를 모두 차감할 수 있다.")
    void deductAllQuantity() {
        // given
        Stock stock = Stock.create(1L, 100);
        int deductQuantity = 100;

        // when
        stock.deductQuantity(deductQuantity);

        // then
        assertThat(stock.getQuantity()).isEqualTo(0);
        assertThat(stock.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("재고보다 많은 수량을 차감하려 하면 예외가 발생한다.")
    void deductQuantityMoreThanStock() {
        // given
        Stock stock = Stock.create(1L, 100);
        int deductQuantity = 150;

        // when & then
        assertThatThrownBy(() -> stock.deductQuantity(deductQuantity))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("재고가 부족합니다.");
    }

    @Test
    @DisplayName("재고에 수량을 추가할 수 있다.")
    void addQuantity() {
        // given
        Stock stock = Stock.create(1L, 100);
        int addQuantity = 50;

        // when
        stock.addQuantity(addQuantity);

        // then
        assertThat(stock.getQuantity()).isEqualTo(150);
    }

    @Test
    @DisplayName("재고가 0일 때 수량을 추가할 수 있다.")
    void addQuantityToEmptyStock() {
        // given
        Stock stock = Stock.create(1L, 0);
        int addQuantity = 100;

        // when
        stock.addQuantity(addQuantity);

        // then
        assertThat(stock.getQuantity()).isEqualTo(100);
        assertThat(stock.isNotEmpty()).isTrue();
    }

    @Test
    @DisplayName("재고가 비어있을 때 isEmpty()는 true를 반환한다.")
    void isEmptyWhenStockIsEmpty() {
        // given
        Stock stock = Stock.create(1L, 0);

        // when
        boolean result = stock.isEmpty();

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("재고가 있을 때 isEmpty()는 false를 반환한다.")
    void isEmptyWhenStockHasQuantity() {
        // given
        Stock stock = Stock.create(1L, 100);

        // when
        boolean result = stock.isEmpty();

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("재고가 비어있을 때 isNotEmpty()는 false를 반환한다.")
    void isNotEmptyWhenStockIsEmpty() {
        // given
        Stock stock = Stock.create(1L, 0);

        // when
        boolean result = stock.isNotEmpty();

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("재고가 있을 때 isNotEmpty()는 true를 반환한다.")
    void isNotEmptyWhenStockHasQuantity() {
        // given
        Stock stock = Stock.create(1L, 100);

        // when
        boolean result = stock.isNotEmpty();

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("재고 차감 후 재고가 비어있으면 isEmpty()는 true를 반환한다.")
    void isEmptyAfterDeductingAllQuantity() {
        // given
        Stock stock = Stock.create(1L, 100);

        // when
        stock.deductQuantity(100);
        boolean result = stock.isEmpty();

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("재고 추가 후 재고가 있으면 isNotEmpty()는 true를 반환한다.")
    void isNotEmptyAfterAddingQuantity() {
        // given
        Stock stock = Stock.create(1L, 0);

        // when
        stock.addQuantity(50);
        boolean result = stock.isNotEmpty();

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("여러 번 재고를 차감할 수 있다.")
    void deductQuantityMultipleTimes() {
        // given
        Stock stock = Stock.create(1L, 100);

        // when
        stock.deductQuantity(20);
        stock.deductQuantity(30);
        stock.deductQuantity(10);

        // then
        assertThat(stock.getQuantity()).isEqualTo(40);
    }

    @Test
    @DisplayName("여러 번 재고를 추가할 수 있다.")
    void addQuantityMultipleTimes() {
        // given
        Stock stock = Stock.create(1L, 50);

        // when
        stock.addQuantity(20);
        stock.addQuantity(30);
        stock.addQuantity(10);

        // then
        assertThat(stock.getQuantity()).isEqualTo(110);
    }

    @Test
    @DisplayName("재고 차감과 추가를 연속으로 할 수 있다.")
    void deductAndAddQuantity() {
        // given
        Stock stock = Stock.create(1L, 100);

        // when
        stock.deductQuantity(30);
        stock.addQuantity(20);
        stock.deductQuantity(10);
        stock.addQuantity(50);

        // then
        assertThat(stock.getQuantity()).isEqualTo(130);
    }

    @Test
    @DisplayName("재고가 0일 때 0을 차감하려 하면 예외가 발생한다.")
    void deductZeroFromEmptyStock() {
        // given
        Stock stock = Stock.create(1L, 0);

        // when & then
        assertThatThrownBy(() -> stock.deductQuantity(1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("재고가 부족합니다.");
    }

    @Test
    @DisplayName("재고가 1일 때 1을 차감하면 재고가 0이 된다.")
    void deductOneFromStockWithOneQuantity() {
        // given
        Stock stock = Stock.create(1L, 1);

        // when
        stock.deductQuantity(1);

        // then
        assertThat(stock.getQuantity()).isEqualTo(0);
        assertThat(stock.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("재고가 1일 때 2를 차감하려 하면 예외가 발생한다.")
    void deductTwoFromStockWithOneQuantity() {
        // given
        Stock stock = Stock.create(1L, 1);

        // when & then
        assertThatThrownBy(() -> stock.deductQuantity(2))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("재고가 부족합니다.");
    }
} 