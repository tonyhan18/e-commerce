package kr.hhplus.be.server.interfaces.balance;

import kr.hhplus.be.server.domain.balance.BalanceResult;
import kr.hhplus.be.server.interfaces.balance.api.BalanceRequest;
import kr.hhplus.be.server.support.ControllerTestSupport;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

class BalanceControllerTest extends ControllerTestSupport{

    @DisplayName("잔액을 조회한다.")
    @Test
    void getBalance() throws Exception {
        // given
        when(balanceService.getBalance(1L))
            .thenReturn(BalanceResult.Balance.of(1_000L));

        // when & then
        mockMvc.perform(
            get("/api/v1/users/{id}/balance", 1L)
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("OK"))
            .andExpect(jsonPath("$.data.amount").value(1_000L));
    }

    @DisplayName("잔액 충전 시, 잔액은 필수이다.")
    @Test
    void chargeBalanceWithoutAmount() throws Exception {
        // given
        BalanceRequest.Charge request = new BalanceRequest.Charge();

        // when & then
        mockMvc.perform(
            post("/api/v1/users/{id}/balance/charge", 1L)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400))
            .andExpect(jsonPath("$.message").value("잔액은 필수 입니다."));
    }

    @DisplayName("잔액 충전 시, 잔액은 양수여야 한다.")
    @ParameterizedTest
    @ValueSource(longs = {-1_000L, 0})
    void chargeBalanceWithNegativeOrZeroAmount(Long amount) throws Exception {
        // given
        BalanceRequest.Charge request = BalanceRequest.Charge.of(amount);

        // when & then
        mockMvc.perform(
            post("/api/v1/users/{id}/balance/charge", 1L)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400))
            .andExpect(jsonPath("$.message").value("잔액은 양수여야 합니다."));
    }

    @DisplayName("잔액을 충전한다.")
    @Test
    void chargeBalance() throws Exception {
        // given
        BalanceRequest.Charge request = BalanceRequest.Charge.of(10_000L);

        // when & then
        mockMvc.perform(
                post("/api/v1/users/{id}/balance/charge", 1L)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("OK"));
    }
}