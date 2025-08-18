package kr.hhplus.be.server.interfaces.products;

import kr.hhplus.be.server.application.product.ProductFacade;
import kr.hhplus.be.server.application.product.ProductResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerTest {

    @Mock
    private ProductFacade productFacade;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    @DisplayName("getProducts API는 정상적으로 동작한다.")
    void getProducts() throws Exception {
        // given
        ProductResult.Products products = mock(ProductResult.Products.class);
        when(productFacade.getProducts()).thenReturn(products);

        // when & then
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk());
        verify(productFacade, times(1)).getProducts();
    }
} 