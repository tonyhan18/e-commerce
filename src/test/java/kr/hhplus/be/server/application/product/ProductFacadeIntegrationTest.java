package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.PaymentInfo;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.product.ProductInfo;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.stock.StockInfo;
import kr.hhplus.be.server.domain.stock.StockService;
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
class ProductFacadeIntegrationTest {

    @Mock
    private ProductService productService;

    @Mock
    private StockService stockService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private ProductFacade productFacade;

    @Test
    @DisplayName("상품 목록 조회 - 성공")
    void getProducts_success() {
        // given
        List<ProductInfo.Product> products = List.of(
            mock(ProductInfo.Product.class),
            mock(ProductInfo.Product.class)
        );
        ProductInfo.Products productInfo = mock(ProductInfo.Products.class);
        when(productInfo.getProducts()).thenReturn(products);
        when(productService.getSellingProducts()).thenReturn(productInfo);

        ProductInfo.Product product1 = products.get(0);
        when(product1.getProductId()).thenReturn(1L);
        when(product1.getProductName()).thenReturn("상품1");
        when(product1.getProductPrice()).thenReturn(10000L);

        ProductInfo.Product product2 = products.get(1);
        when(product2.getProductId()).thenReturn(2L);
        when(product2.getProductName()).thenReturn("상품2");
        when(product2.getProductPrice()).thenReturn(20000L);

        StockInfo.Stock stock1 = mock(StockInfo.Stock.class);
        when(stock1.getQuantity()).thenReturn(10);
        when(stockService.getStock(1L)).thenReturn(stock1);

        StockInfo.Stock stock2 = mock(StockInfo.Stock.class);
        when(stock2.getQuantity()).thenReturn(5);
        when(stockService.getStock(2L)).thenReturn(stock2);

        // when
        ProductResult.Products result = productFacade.getProducts();

        // then
        assertThat(result).isNotNull();
        verify(productService, times(1)).getSellingProducts();
        verify(stockService, times(2)).getStock(any(Long.class));
    }

    @Test
    @DisplayName("상품 목록 조회 - 빈 결과")
    void getProducts_empty() {
        // given
        List<ProductInfo.Product> products = List.of();
        ProductInfo.Products productInfo = mock(ProductInfo.Products.class);
        when(productInfo.getProducts()).thenReturn(products);
        when(productService.getSellingProducts()).thenReturn(productInfo);

        // when
        ProductResult.Products result = productFacade.getProducts();

        // then
        assertThat(result).isNotNull();
        verify(productService, times(1)).getSellingProducts();
        verify(stockService, never()).getStock(any(Long.class));
    }

    @Test
    @DisplayName("인기 상품 목록 조회 - 성공")
    void getPopularProducts_success() {
        // given
        List<Long> orderIds = List.of(1L, 2L);
        PaymentInfo.Orders completedOrders = mock(PaymentInfo.Orders.class);
        when(completedOrders.getOrderIds()).thenReturn(orderIds);
        when(paymentService.getCompletedOrdersBetweenDays(3)).thenReturn(completedOrders);

        List<Long> productIds = List.of(1L, 2L);
        OrderInfo.TopPaidProducts topPaidProducts = mock(OrderInfo.TopPaidProducts.class);
        when(topPaidProducts.getProductIds()).thenReturn(productIds);
        when(orderService.getTopPaidProducts(any())).thenReturn(topPaidProducts);

        List<ProductInfo.Product> products = List.of(
            mock(ProductInfo.Product.class),
            mock(ProductInfo.Product.class)
        );
        ProductInfo.Products productInfo = mock(ProductInfo.Products.class);
        when(productInfo.getProducts()).thenReturn(products);
        when(productService.getProducts(any())).thenReturn(productInfo);

        ProductInfo.Product product1 = products.get(0);
        when(product1.getProductId()).thenReturn(1L);
        when(product1.getProductName()).thenReturn("인기 상품1");
        when(product1.getProductPrice()).thenReturn(15000L);

        ProductInfo.Product product2 = products.get(1);
        when(product2.getProductId()).thenReturn(2L);
        when(product2.getProductName()).thenReturn("인기 상품2");
        when(product2.getProductPrice()).thenReturn(25000L);

        StockInfo.Stock stock1 = mock(StockInfo.Stock.class);
        when(stock1.getQuantity()).thenReturn(8);
        when(stockService.getStock(1L)).thenReturn(stock1);

        StockInfo.Stock stock2 = mock(StockInfo.Stock.class);
        when(stock2.getQuantity()).thenReturn(3);
        when(stockService.getStock(2L)).thenReturn(stock2);

        // when
        ProductResult.Products result = productFacade.getPopularProducts();

        // then
        assertThat(result).isNotNull();
        verify(paymentService, times(1)).getCompletedOrdersBetweenDays(3);
        verify(orderService, times(1)).getTopPaidProducts(any());
        verify(productService, times(1)).getProducts(any());
        verify(stockService, times(2)).getStock(any(Long.class));
    }

    @Test
    @DisplayName("인기 상품 목록 조회 - 빈 주문 결과")
    void getPopularProducts_emptyOrders() {
        // given
        List<Long> orderIds = List.of();
        PaymentInfo.Orders completedOrders = mock(PaymentInfo.Orders.class);
        when(completedOrders.getOrderIds()).thenReturn(orderIds);
        when(paymentService.getCompletedOrdersBetweenDays(3)).thenReturn(completedOrders);

        List<Long> productIds = List.of();
        OrderInfo.TopPaidProducts topPaidProducts = mock(OrderInfo.TopPaidProducts.class);
        when(topPaidProducts.getProductIds()).thenReturn(productIds);
        when(orderService.getTopPaidProducts(any())).thenReturn(topPaidProducts);

        List<ProductInfo.Product> products = List.of();
        ProductInfo.Products productInfo = mock(ProductInfo.Products.class);
        when(productInfo.getProducts()).thenReturn(products);
        when(productService.getProducts(any())).thenReturn(productInfo);

        // when
        ProductResult.Products result = productFacade.getPopularProducts();

        // then
        assertThat(result).isNotNull();
        verify(paymentService, times(1)).getCompletedOrdersBetweenDays(3);
        verify(orderService, times(1)).getTopPaidProducts(any());
        verify(productService, times(1)).getProducts(any());
        verify(stockService, never()).getStock(any(Long.class));
    }

    @Test
    @DisplayName("인기 상품 목록 조회 - 재고 정보 없음")
    void getPopularProducts_stockNotFound() {
        // given
        List<Long> orderIds = List.of(1L);
        PaymentInfo.Orders completedOrders = mock(PaymentInfo.Orders.class);
        when(completedOrders.getOrderIds()).thenReturn(orderIds);
        when(paymentService.getCompletedOrdersBetweenDays(3)).thenReturn(completedOrders);

        List<Long> productIds = List.of(1L);
        OrderInfo.TopPaidProducts topPaidProducts = mock(OrderInfo.TopPaidProducts.class);
        when(topPaidProducts.getProductIds()).thenReturn(productIds);
        when(orderService.getTopPaidProducts(any())).thenReturn(topPaidProducts);

        List<ProductInfo.Product> products = List.of(
            mock(ProductInfo.Product.class)
        );
        ProductInfo.Products productInfo = mock(ProductInfo.Products.class);
        when(productInfo.getProducts()).thenReturn(products);
        when(productService.getProducts(any())).thenReturn(productInfo);

        ProductInfo.Product product = products.get(0);
        when(product.getProductId()).thenReturn(1L);
        when(product.getProductName()).thenReturn("상품1");
        when(product.getProductPrice()).thenReturn(10000L);

        when(stockService.getStock(1L))
            .thenThrow(new IllegalArgumentException("Stock not found"));

        // when & then
        assertThatThrownBy(() -> productFacade.getPopularProducts())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Stock not found");

        verify(paymentService, times(1)).getCompletedOrdersBetweenDays(3);
        verify(orderService, times(1)).getTopPaidProducts(any());
        verify(productService, times(1)).getProducts(any());
        verify(stockService, times(1)).getStock(1L);
    }
} 