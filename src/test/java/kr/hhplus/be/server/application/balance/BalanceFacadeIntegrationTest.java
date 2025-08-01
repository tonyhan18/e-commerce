package kr.hhplus.be.server.application.balance;

import kr.hhplus.be.server.domain.balance.BalanceInfo;
import kr.hhplus.be.server.domain.balance.BalanceService;
import kr.hhplus.be.server.domain.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BalanceFacadeIntegrationTest {

    @Mock
    private UserService userService;

    @Mock
    private BalanceService balanceService;

    @InjectMocks
    private BalanceFacade balanceFacade;

    @Test
    @DisplayName("잔액 충전 - 성공")
    void chargeBalance_success() {
        // given
        Long userId = 1L;
        Long amount = 10000L;
        BalanceCriteria.Charge criteria = BalanceCriteria.Charge.of(userId, amount);

        // when
        balanceFacade.chargeBalance(criteria);

        // then
        verify(userService, times(1)).getUser(userId);
        verify(balanceService, times(1)).chargeBalance(any());
    }

    @Test
    @DisplayName("잔액 충전 - 사용자 없음")
    void chargeBalance_userNotFound() {
        // given
        Long userId = 999L;
        Long amount = 10000L;
        BalanceCriteria.Charge criteria = BalanceCriteria.Charge.of(userId, amount);
        doThrow(new IllegalArgumentException("User not found"))
            .when(userService).getUser(userId);

        // when & then
        assertThatThrownBy(() -> balanceFacade.chargeBalance(criteria))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("User not found");
        verify(userService, times(1)).getUser(userId);
        verify(balanceService, never()).chargeBalance(any());
    }

    @Test
    @DisplayName("잔액 조회 - 성공")
    void getBalance_success() {
        // given
        Long userId = 1L;
        Long balanceAmount = 50000L;
        BalanceInfo.Balance mockBalance = mock(BalanceInfo.Balance.class);
        when(mockBalance.getBalance()).thenReturn(balanceAmount);
        when(balanceService.getBalance(userId)).thenReturn(mockBalance);

        // when
        BalanceResult.Balance result = balanceFacade.getBalance(userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getBalance()).isEqualTo(balanceAmount);
        verify(userService, times(1)).getUser(userId);
        verify(balanceService, times(1)).getBalance(userId);
    }

    @Test
    @DisplayName("잔액 조회 - 사용자 없음")
    void getBalance_userNotFound() {
        // given
        Long userId = 999L;
        doThrow(new IllegalArgumentException("User not found"))
            .when(userService).getUser(userId);

        // when & then
        assertThatThrownBy(() -> balanceFacade.getBalance(userId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("User not found");
        verify(userService, times(1)).getUser(userId);
        verify(balanceService, never()).getBalance(any());
    }
} 