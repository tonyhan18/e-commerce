package kr.hhplus.be.server.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    @Test
    @DisplayName("상품을 생성할 수 있다.")
    void createProduct() {
        // given
        String name = "테스트 상품";
        long price = 10000L;
        ProductSellingStatus sellStatus = ProductSellingStatus.SELLING;

        // when
        Product product = Product.create(name, price, sellStatus);

        // then
        assertThat(product.getName()).isEqualTo(name);
        assertThat(product.getPrice()).isEqualTo(price);
        assertThat(product.getSellStatus()).isEqualTo(sellStatus);
        assertThat(product.getId()).isNull(); // ID는 저장 시점에 생성됨
    }

    @Test
    @DisplayName("상품명이 null이면 예외가 발생한다.")
    void createProductWithNullName() {
        // given
        String name = null;
        long price = 10000L;
        ProductSellingStatus sellStatus = ProductSellingStatus.SELLING;

        // when & then
        assertThatThrownBy(() -> Product.create(name, price, sellStatus))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("상품 이름은 필수입니다.");
    }

    @Test
    @DisplayName("상품명이 빈 문자열이면 예외가 발생한다.")
    void createProductWithEmptyName() {
        // given
        String name = "";
        long price = 10000L;
        ProductSellingStatus sellStatus = ProductSellingStatus.SELLING;

        // when & then
        assertThatThrownBy(() -> Product.create(name, price, sellStatus))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("상품 이름은 필수입니다.");
    }

    @Test
    @DisplayName("상품명이 공백만 있으면 예외가 발생한다.")
    void createProductWithBlankName() {
        // given
        String name = "   ";
        long price = 10000L;
        ProductSellingStatus sellStatus = ProductSellingStatus.SELLING;

        // when & then
        assertThatThrownBy(() -> Product.create(name, price, sellStatus))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("상품 이름은 필수입니다.");
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1, -1000})
    @DisplayName("가격이 0 이하이면 예외가 발생한다.")
    void createProductWithInvalidPrice(long invalidPrice) {
        // given
        String name = "테스트 상품";
        ProductSellingStatus sellStatus = ProductSellingStatus.SELLING;

        // when & then
        assertThatThrownBy(() -> Product.create(name, invalidPrice, sellStatus))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("상품 가격은 0보다 커야 합니다.");
    }

    @Test
    @DisplayName("판매 상태가 null이면 예외가 발생한다.")
    void createProductWithNullSellStatus() {
        // given
        String name = "테스트 상품";
        long price = 10000L;
        ProductSellingStatus sellStatus = null;

        // when & then
        assertThatThrownBy(() -> Product.create(name, price, sellStatus))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("상품 판매 상태는 필수입니다.");
    }

    @Test
    @DisplayName("판매 중인 상품은 판매 가능하다.")
    void cannotSellingWhenSelling() {
        // given
        Product product = Product.create("테스트 상품", 10000L, ProductSellingStatus.SELLING);

        // when
        boolean result = product.cannotSelling();

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("판매 보류인 상품은 판매 불가능하다.")
    void cannotSellingWhenHold() {
        // given
        Product product = Product.create("테스트 상품", 10000L, ProductSellingStatus.HOLD);

        // when
        boolean result = product.cannotSelling();

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("판매 중지인 상품은 판매 불가능하다.")
    void cannotSellingWhenStopSelling() {
        // given
        Product product = Product.create("테스트 상품", 10000L, ProductSellingStatus.STOP_SELLING);

        // when
        boolean result = product.cannotSelling();

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("빌더 패턴으로 상품을 생성할 수 있다.")
    void createProductWithBuilder() {
        // given
        String name = "빌더 테스트 상품";
        long price = 20000L;
        ProductSellingStatus sellStatus = ProductSellingStatus.SELLING;

        // when
        Product product = Product.builder()
            .name(name)
            .price(price)
            .sellStatus(sellStatus)
            .build();

        // then
        assertThat(product.getName()).isEqualTo(name);
        assertThat(product.getPrice()).isEqualTo(price);
        assertThat(product.getSellStatus()).isEqualTo(sellStatus);
    }

    @Test
    @DisplayName("상품 ID가 있는 빌더로 상품을 생성할 수 있다.")
    void createProductWithBuilderAndId() {
        // given
        Long productId = 1L;
        String name = "ID 테스트 상품";
        long price = 30000L;
        ProductSellingStatus sellStatus = ProductSellingStatus.SELLING;

        // when
        Product product = Product.builder()
            .id(productId)
            .name(name)
            .price(price)
            .sellStatus(sellStatus)
            .build();

        // then
        assertThat(product.getId()).isEqualTo(productId);
        assertThat(product.getName()).isEqualTo(name);
        assertThat(product.getPrice()).isEqualTo(price);
        assertThat(product.getSellStatus()).isEqualTo(sellStatus);
    }

    @Test
    @DisplayName("모든 판매 상태로 상품을 생성할 수 있다.")
    void createProductWithAllSellingStatuses() {
        // given & when & then
        Product sellingProduct = Product.create("판매중 상품", 10000L, ProductSellingStatus.SELLING);
        Product holdProduct = Product.create("판매보류 상품", 20000L, ProductSellingStatus.HOLD);
        Product stopProduct = Product.create("판매중지 상품", 30000L, ProductSellingStatus.STOP_SELLING);

        assertThat(sellingProduct.getSellStatus()).isEqualTo(ProductSellingStatus.SELLING);
        assertThat(holdProduct.getSellStatus()).isEqualTo(ProductSellingStatus.HOLD);
        assertThat(stopProduct.getSellStatus()).isEqualTo(ProductSellingStatus.STOP_SELLING);
    }
} 