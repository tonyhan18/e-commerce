package kr.hhplus.be.server.domain.balance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BalanceServiceIntegrationTest {

    @Mock
    private BalanceRepository balanceRepository;

    @InjectMocks
    private BalanceService balanceService;

    private Balance testBalance;

    @BeforeEach
    void setUp() {
        testBalance = Balance.create(1L, 1000L);
    }

    @Test
    @DisplayName("잔액 조회 - 기존 잔액이 있는 경우")
    void getBalance_existingBalance() {
        // given
        Long userId = 1L;
        when(balanceRepository.findOptionalByUserId(userId))
                .thenReturn(Optional.of(testBalance));

        // when
        BalanceInfo.Balance result = balanceService.getBalance(userId);

        // then
        assertThat(result.getBalance()).isEqualTo(1000L);
        verify(balanceRepository, times(1)).findOptionalByUserId(userId);
    }

    @Test
    @DisplayName("잔액 조회 - 잔액이 없는 경우")
    void getBalance_noBalance() {
        // given
        Long userId = 999L;
        when(balanceRepository.findOptionalByUserId(userId))
                .thenReturn(Optional.empty());

        // when
        BalanceInfo.Balance result = balanceService.getBalance(userId);

        // then
        assertThat(result.getBalance()).isEqualTo(0L);
        verify(balanceRepository, times(1)).findOptionalByUserId(userId);
    }

    @Test
    @DisplayName("잔액 충전 - 기존 잔액이 있는 경우")
    void chargeBalance_existingBalance() {
        // given
        Long userId = 1L;
        Long chargeAmount = 500L;
        BalanceCommand.Charge command = BalanceCommand.Charge.of(userId, chargeAmount);
        
        when(balanceRepository.findOptionalByUserId(userId))
                .thenReturn(Optional.of(testBalance));

        // when
        balanceService.chargeBalance(command);

        // then
        assertThat(testBalance.getBalance()).isEqualTo(1500L);
        verify(balanceRepository, times(1)).findOptionalByUserId(userId);
        // 기존 잔액이 있을 때는 save가 호출되지 않음 (JPA의 dirty checking 때문)
        verify(balanceRepository, never()).save(any(Balance.class));
    }

    @Test
    @DisplayName("잔액 충전 - 새로운 사용자의 경우")
    void chargeBalance_newUser() {
        // given
        Long userId = 2L;
        Long chargeAmount = 2000L;
        BalanceCommand.Charge command = BalanceCommand.Charge.of(userId, chargeAmount);
        
        when(balanceRepository.findOptionalByUserId(userId))
                .thenReturn(Optional.empty());
        when(balanceRepository.save(any(Balance.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        balanceService.chargeBalance(command);

        // then
        verify(balanceRepository, times(1)).findOptionalByUserId(userId);
        verify(balanceRepository, times(1)).save(any(Balance.class));
    }

    @Test
    @DisplayName("잔액 사용 - 기존 잔액이 있는 경우")
    void useBalance_existingBalance() {
        // given
        Long userId = 1L;
        Long useAmount = 300L;
        BalanceCommand.Use command = BalanceCommand.Use.of(userId, useAmount);
        
        when(balanceRepository.findOptionalByUserId(userId))
                .thenReturn(Optional.of(testBalance));

        // when
        balanceService.useBalance(command);

        // then
        assertThat(testBalance.getBalance()).isEqualTo(700L);
        verify(balanceRepository, times(1)).findOptionalByUserId(userId);
        // 기존 잔액이 있을 때는 save가 호출되지 않음 (JPA의 dirty checking 때문)
        verify(balanceRepository, never()).save(any(Balance.class));
    }

    @Test
    @DisplayName("잔액 사용 - 새로운 사용자의 경우")
    void useBalance_newUser() {
        // given
        Long userId = 3L;
        Long useAmount = 1000L;
        BalanceCommand.Use command = BalanceCommand.Use.of(userId, useAmount);
        
        when(balanceRepository.findOptionalByUserId(userId))
                .thenReturn(Optional.empty());
        when(balanceRepository.save(any(Balance.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        balanceService.useBalance(command);

        // then
        verify(balanceRepository, times(1)).findOptionalByUserId(userId);
        verify(balanceRepository, times(1)).save(any(Balance.class));
    }
}