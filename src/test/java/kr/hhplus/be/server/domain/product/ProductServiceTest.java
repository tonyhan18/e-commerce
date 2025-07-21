package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

class ProductServiceTest extends MockTestSupport {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRespository productRepository;

    @Test
    @DisplayName("주문 상품 정보를 조회할 수 있다.")
    void getOrderProducts() {
        // given
        Long productId = 1L;
        String productName = "테스트 상품";
        Long productPrice = 10000L;
        int quantity = 2;

        Product product = Product.builder()
            .id(productId)
            .name(productName)
            .price(productPrice)
            .sellStatus(ProductSellingStatus.SELLING)
            .build();

        ProductCommand.OrderProduct orderProductCommand = ProductCommand.OrderProduct.of(productId, quantity);
        ProductCommand.OrderProducts orderProductsCommand = ProductCommand.OrderProducts.of(List.of(orderProductCommand));

        when(productRepository.findById(productId)).thenReturn(product);

        // when
        ProductInfo.OrderProducts result = productService.getOrderProducts(orderProductsCommand);

        // then
        assertThat(result.getProducts()).hasSize(1);
        ProductInfo.OrderProduct orderProduct = result.getProducts().get(0);
        assertThat(orderProduct.getProductId()).isEqualTo(productId);
        assertThat(orderProduct.getProductName()).isEqualTo(productName);
        assertThat(orderProduct.getProductPrice()).isEqualTo(productPrice);
        assertThat(orderProduct.getQuantity()).isEqualTo(quantity);
    }

    @Test
    @DisplayName("주문 불가능한 상품이 포함되어 있으면 예외가 발생한다.")
    void getOrderProductsWithCannotSellingProduct() {
        // given
        Long productId = 1L;
        int quantity = 2;

        Product product = Product.builder()
            .id(productId)
            .name("판매 중지 상품")
            .price(10000L)
            .sellStatus(ProductSellingStatus.STOP_SELLING)
            .build();

        ProductCommand.OrderProduct orderProductCommand = ProductCommand.OrderProduct.of(productId, quantity);
        ProductCommand.OrderProducts orderProductsCommand = ProductCommand.OrderProducts.of(List.of(orderProductCommand));

        when(productRepository.findById(productId)).thenReturn(product);

        // when & then
        assertThatThrownBy(() -> productService.getOrderProducts(orderProductsCommand))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("주문 불가한 상품이 포함되어 있습니다.");
    }

    @Test
    @DisplayName("판매 중인 상품 목록을 조회할 수 있다.")
    void getSellingProducts() {
        // given
        Product product1 = Product.builder()
            .id(1L)
            .name("판매중 상품1")
            .price(10000L)
            .sellStatus(ProductSellingStatus.SELLING)
            .build();

        Product product2 = Product.builder()
            .id(2L)
            .name("판매중 상품2")
            .price(20000L)
            .sellStatus(ProductSellingStatus.SELLING)
            .build();

        List<Product> products = List.of(product1, product2);

        when(productRepository.findSellingStatusIn(anyList())).thenReturn(products);

        // when
        ProductInfo.Products result = productService.getSellingProducts();

        // then
        assertThat(result.getProducts()).hasSize(2);
        
        ProductInfo.Product resultProduct1 = result.getProducts().get(0);
        assertThat(resultProduct1.getProductId()).isEqualTo(1L);
        assertThat(resultProduct1.getProductName()).isEqualTo("판매중 상품1");
        assertThat(resultProduct1.getProductPrice()).isEqualTo(10000L);

        ProductInfo.Product resultProduct2 = result.getProducts().get(1);
        assertThat(resultProduct2.getProductId()).isEqualTo(2L);
        assertThat(resultProduct2.getProductName()).isEqualTo("판매중 상품2");
        assertThat(resultProduct2.getProductPrice()).isEqualTo(20000L);
    }

    @Test
    @DisplayName("특정 상품 ID 목록으로 상품 정보를 조회할 수 있다.")
    void getProducts() {
        // given
        Long productId1 = 1L;
        Long productId2 = 2L;
        List<Long> productIds = List.of(productId1, productId2);

        Product product1 = Product.builder()
            .id(productId1)
            .name("상품1")
            .price(10000L)
            .sellStatus(ProductSellingStatus.SELLING)
            .build();

        Product product2 = Product.builder()
            .id(productId2)
            .name("상품2")
            .price(20000L)
            .sellStatus(ProductSellingStatus.SELLING)
            .build();

        List<Product> products = List.of(product1, product2);

        ProductCommand.Products command = ProductCommand.Products.of(productIds);

        when(productRepository.findByIds(productIds)).thenReturn(products);

        // when
        ProductInfo.Products result = productService.getProducts(command);

        // then
        assertThat(result.getProducts()).hasSize(2);
        
        ProductInfo.Product resultProduct1 = result.getProducts().get(0);
        assertThat(resultProduct1.getProductId()).isEqualTo(productId1);
        assertThat(resultProduct1.getProductName()).isEqualTo("상품1");
        assertThat(resultProduct1.getProductPrice()).isEqualTo(10000L);

        ProductInfo.Product resultProduct2 = result.getProducts().get(1);
        assertThat(resultProduct2.getProductId()).isEqualTo(productId2);
        assertThat(resultProduct2.getProductName()).isEqualTo("상품2");
        assertThat(resultProduct2.getProductPrice()).isEqualTo(20000L);
    }

    @Test
    @DisplayName("여러 주문 상품 정보를 조회할 수 있다.")
    void getMultipleOrderProducts() {
        // given
        Product product1 = Product.builder()
            .id(1L)
            .name("상품1")
            .price(10000L)
            .sellStatus(ProductSellingStatus.SELLING)
            .build();

        Product product2 = Product.builder()
            .id(2L)
            .name("상품2")
            .price(20000L)
            .sellStatus(ProductSellingStatus.SELLING)
            .build();

        ProductCommand.OrderProduct orderProduct1 = ProductCommand.OrderProduct.of(1L, 2);
        ProductCommand.OrderProduct orderProduct2 = ProductCommand.OrderProduct.of(2L, 1);
        ProductCommand.OrderProducts orderProductsCommand = ProductCommand.OrderProducts.of(List.of(orderProduct1, orderProduct2));

        when(productRepository.findById(1L)).thenReturn(product1);
        when(productRepository.findById(2L)).thenReturn(product2);

        // when
        ProductInfo.OrderProducts result = productService.getOrderProducts(orderProductsCommand);

        // then
        assertThat(result.getProducts()).hasSize(2);
        
        ProductInfo.OrderProduct resultOrderProduct1 = result.getProducts().get(0);
        assertThat(resultOrderProduct1.getProductId()).isEqualTo(1L);
        assertThat(resultOrderProduct1.getProductName()).isEqualTo("상품1");
        assertThat(resultOrderProduct1.getProductPrice()).isEqualTo(10000L);
        assertThat(resultOrderProduct1.getQuantity()).isEqualTo(2);

        ProductInfo.OrderProduct resultOrderProduct2 = result.getProducts().get(1);
        assertThat(resultOrderProduct2.getProductId()).isEqualTo(2L);
        assertThat(resultOrderProduct2.getProductName()).isEqualTo("상품2");
        assertThat(resultOrderProduct2.getProductPrice()).isEqualTo(20000L);
        assertThat(resultOrderProduct2.getQuantity()).isEqualTo(1);
    }

    @Test
    @DisplayName("판매 보류 상태의 상품은 주문할 수 없다.")
    void getOrderProductsWithHoldProduct() {
        // given
        Long productId = 1L;
        int quantity = 1;

        Product product = Product.builder()
            .id(productId)
            .name("판매 보류 상품")
            .price(10000L)
            .sellStatus(ProductSellingStatus.HOLD)
            .build();

        ProductCommand.OrderProduct orderProductCommand = ProductCommand.OrderProduct.of(productId, quantity);
        ProductCommand.OrderProducts orderProductsCommand = ProductCommand.OrderProducts.of(List.of(orderProductCommand));

        when(productRepository.findById(productId)).thenReturn(product);

        // when & then
        assertThatThrownBy(() -> productService.getOrderProducts(orderProductsCommand))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("주문 불가한 상품이 포함되어 있습니다.");
    }

    @Test
    @DisplayName("빈 상품 목록으로 조회할 수 있다.")
    void getEmptyProducts() {
        // given
        ProductCommand.Products command = ProductCommand.Products.of(List.of());
        when(productRepository.findByIds(List.of())).thenReturn(List.of());

        // when
        ProductInfo.Products result = productService.getProducts(command);

        // then
        assertThat(result.getProducts()).isEmpty();
    }

    @Test
    @DisplayName("빈 주문 상품 목록으로 조회할 수 있다.")
    void getEmptyOrderProducts() {
        // given
        ProductCommand.OrderProducts command = ProductCommand.OrderProducts.of(List.of());

        // when
        ProductInfo.OrderProducts result = productService.getOrderProducts(command);

        // then
        assertThat(result.getProducts()).isEmpty();
    }
} 