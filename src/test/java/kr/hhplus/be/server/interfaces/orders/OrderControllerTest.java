package kr.hhplus.be.server.interfaces.orders;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.application.order.OrderFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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
        OrderRequest.OrderPayment request = mock(OrderRequest.OrderPayment.class);
        when(request.toCriteria()).thenReturn(mock(kr.hhplus.be.server.application.order.OrderCriteria.OrderPayment.class));
        String json = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
        verify(orderFacade, times(1)).orderPayment(any());
    }
} 