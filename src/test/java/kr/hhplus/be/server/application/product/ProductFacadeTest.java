package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.payment.PaymentInfo;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.product.ProductCommand;
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

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductFacadeTest {

    @Mock private ProductService productService;
    @Mock private StockService stockService;
    @Mock private PaymentService paymentService;
    @Mock private OrderService orderService;
    @InjectMocks private ProductFacade productFacade;

    @Test
    @DisplayName("getProducts 호출 시 판매중 상품과 재고가 반환된다.")
    void getProducts() {
        // given
        ProductInfo.Product product1 = mock(ProductInfo.Product.class);
        ProductInfo.Product product2 = mock(ProductInfo.Product.class);
        when(product1.getProductId()).thenReturn(1L);
        when(product1.getProductName()).thenReturn("상품1");
        when(product1.getProductPrice()).thenReturn(1000L);
        when(product2.getProductId()).thenReturn(2L);
        when(product2.getProductName()).thenReturn("상품2");
        when(product2.getProductPrice()).thenReturn(2000L);
        List<ProductInfo.Product> productList = Arrays.asList(product1, product2);
        ProductInfo.Products products = mock(ProductInfo.Products.class);
        when(products.getProducts()).thenReturn(productList);
        when(productService.getSellingProducts()).thenReturn(products);
        when(stockService.getStock(1L)).thenReturn(mock(StockInfo.Stock.class));
        when(stockService.getStock(2L)).thenReturn(mock(StockInfo.Stock.class));

        // when
        ProductResult.Products result = productFacade.getProducts();

        // then
        assertThat(result).isNotNull();
        verify(productService, times(1)).getSellingProducts();
        verify(stockService, times(1)).getStock(1L);
        verify(stockService, times(1)).getStock(2L);
    }

    @Test
    @DisplayName("getPopularProducts 호출 시 인기상품과 재고가 반환된다.")
    void getPopularProducts() {
        // given
        List<Long> orderIds = Arrays.asList(1L, 2L, 3L);
        PaymentInfo.Orders completedOrders = mock(PaymentInfo.Orders.class);
        when(completedOrders.getOrderIds()).thenReturn(orderIds);
        when(paymentService.getCompletedOrdersBetweenDays(anyInt())).thenReturn(completedOrders);

        List<Long> productIds = Arrays.asList(10L, 20L, 30L);
        OrderInfo.TopPaidProducts topPaidProducts = mock(OrderInfo.TopPaidProducts.class);
        when(topPaidProducts.getProductIds()).thenReturn(productIds);
        when(orderService.getTopPaidProducts(any(OrderCommand.TopOrders.class))).thenReturn(topPaidProducts);

        ProductInfo.Product product1 = mock(ProductInfo.Product.class);
        ProductInfo.Product product2 = mock(ProductInfo.Product.class);
        ProductInfo.Product product3 = mock(ProductInfo.Product.class);
        when(product1.getProductId()).thenReturn(10L);
        when(product2.getProductId()).thenReturn(20L);
        when(product3.getProductId()).thenReturn(30L);
        when(product1.getProductName()).thenReturn("인기상품1");
        when(product2.getProductName()).thenReturn("인기상품2");
        when(product3.getProductName()).thenReturn("인기상품3");
        when(product1.getProductPrice()).thenReturn(10000L);
        when(product2.getProductPrice()).thenReturn(20000L);
        when(product3.getProductPrice()).thenReturn(30000L);
        List<ProductInfo.Product> productList = Arrays.asList(product1, product2, product3);
        ProductInfo.Products products = mock(ProductInfo.Products.class);
        when(products.getProducts()).thenReturn(productList);
        when(productService.getProducts(any(ProductCommand.Products.class))).thenReturn(products);
        when(stockService.getStock(10L)).thenReturn(mock(StockInfo.Stock.class));
        when(stockService.getStock(20L)).thenReturn(mock(StockInfo.Stock.class));
        when(stockService.getStock(30L)).thenReturn(mock(StockInfo.Stock.class));

        // when
        ProductResult.Products result = productFacade.getPopularProducts();

        // then
        assertThat(result).isNotNull();
        verify(paymentService, times(1)).getCompletedOrdersBetweenDays(anyInt());
        verify(orderService, times(1)).getTopPaidProducts(any(OrderCommand.TopOrders.class));
        verify(productService, times(1)).getProducts(any(ProductCommand.Products.class));
        verify(stockService, times(1)).getStock(10L);
        verify(stockService, times(1)).getStock(20L);
        verify(stockService, times(1)).getStock(30L);
    }
} 