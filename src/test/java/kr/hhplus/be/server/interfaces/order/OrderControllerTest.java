package kr.hhplus.be.server.interfaces.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.application.order.OrderFacade;
import kr.hhplus.be.server.interfaces.orders.OrderController;
import kr.hhplus.be.server.interfaces.orders.OrderRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerTest {

    @Mock
    private OrderFacade orderFacade;

    @InjectMocks
    private OrderController orderController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    @Test
    @DisplayName("orderPayment API는 정상적으로 동작한다.")
    void orderPayment() throws Exception {
        // given
        OrderRequest.OrderProduct orderProduct = OrderRequest.OrderProduct.of(1L, 2);

        OrderRequest.OrderPayment request = OrderRequest.OrderPayment.of(1L, Arrays.asList(orderProduct), null);

        String json = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
        verify(orderFacade, times(1)).orderPayment(any());
    }

    @Test
    @DisplayName("쿠폰이 있는 주문 결제 API는 정상적으로 동작한다.")
    void orderPaymentWithCoupon() throws Exception {
        // given
        OrderRequest.OrderProduct orderProduct = OrderRequest.OrderProduct.of(1L, 1);

        OrderRequest.OrderPayment request = OrderRequest.OrderPayment.of(1L, Arrays.asList(orderProduct), 1L);

        String json = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
        verify(orderFacade, times(1)).orderPayment(any());
    }
} 