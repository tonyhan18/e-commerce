package kr.hhplus.be.server.interfaces.balance;

import kr.hhplus.be.server.application.balance.BalanceFacade;
import kr.hhplus.be.server.application.balance.BalanceResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BalanceController.class)
class BalanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BalanceFacade balanceFacade;

    @Test
    @DisplayName("잔액 조회 API는 정상적으로 잔액을 반환한다.")
    void getBalance() throws Exception {
        // given
        Long userId = 1L;
        BalanceResult.Balance balance = mock(BalanceResult.Balance.class);
        when(balanceFacade.getBalance(userId)).thenReturn(balance);

        // when & then
        mockMvc.perform(get("/api/v1/users/{userId}/balance", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
        verify(balanceFacade, times(1)).getBalance(userId);
    }

    @Test
    @DisplayName("잔액 충전 API는 정상적으로 동작한다.")
    void updateBalance() throws Exception {
        // given
        Long userId = 2L;
        String requestJson = "{\"amount\": 10000}";

        // when & then
        mockMvc.perform(post("/api/v1/users/{userId}/balance/charge", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
        verify(balanceFacade, times(1)).chargeBalance(any());
    }
}