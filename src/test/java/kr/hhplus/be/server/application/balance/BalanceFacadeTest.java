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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BalanceFacadeTest {

    @Mock private UserService userService;
    @Mock private BalanceService balanceService;
    @InjectMocks private BalanceFacade balanceFacade;

    @Test
    @DisplayName("잔액 충전 시 UserService와 BalanceService가 호출된다.")
    void chargeBalance() {
        // given
        BalanceCriteria.Charge criteria = mock(BalanceCriteria.Charge.class);
        when(criteria.getUserId()).thenReturn(1L);
        when(criteria.toCommand()).thenReturn(null); // 실제 Command 객체 필요시 수정

        // when
        balanceFacade.chargeBalance(criteria);

        // then
        verify(userService, times(1)).getUser(1L);
        verify(balanceService, times(1)).chargeBalance(null);
    }

    @Test
    @DisplayName("잔액 조회 시 UserService와 BalanceService가 호출되고 결과가 반환된다.")
    void getBalance() {
        // given
        Long userId = 2L;
        BalanceInfo.Balance mockBalance = mock(BalanceInfo.Balance.class);
        when(balanceService.getBalance(userId)).thenReturn(mockBalance);
        when(mockBalance.getBalance()).thenReturn(5000L);

        // when
        BalanceResult.Balance result = balanceFacade.getBalance(userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getBalance()).isEqualTo(5000L);
        verify(userService, times(1)).getUser(userId);
        verify(balanceService, times(1)).getBalance(userId);
    }
} 
