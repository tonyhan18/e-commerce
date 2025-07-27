package kr.hhplus.be.server.domain.balance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class BalanceServiceTest {
    private BalanceRepository balanceRepository;
    private BalanceService balanceService;

    @BeforeEach
    void setUp() {
        balanceRepository = mock(BalanceRepository.class);
        balanceService = new BalanceService(balanceRepository);
    }

    @Test
    @DisplayName("getBalance - 잔액이 존재하는 경우")
    void getBalance_exists() {
        Balance balance = Balance.create(1L, 5000L);
        when(balanceRepository.findOptionalByUserId(1L)).thenReturn(Optional.of(balance));

        BalanceInfo.Balance result = balanceService.getBalance(1L);
        assertThat(result.getBalance()).isEqualTo(5000L);
    }

    @Test
    @DisplayName("getBalance - 잔액이 없는 경우 0 반환")
    void getBalance_notExists() {
        when(balanceRepository.findOptionalByUserId(2L)).thenReturn(Optional.empty());

        BalanceInfo.Balance result = balanceService.getBalance(2L);
        assertThat(result.getBalance()).isEqualTo(0L);
    }

    @Test
    @DisplayName("chargeBalance - 기존 잔액이 있을 때 충전")
    void chargeBalance_exists() {
        Balance balance = spy(Balance.create(1L, 1000L));
        BalanceCommand.Charge command = BalanceCommand.Charge.of(1L, 500L);
        when(balanceRepository.findOptionalByUserId(1L)).thenReturn(Optional.of(balance));

        balanceService.chargeBalance(command);
        verify(balance).charge(500L);
        verify(balanceRepository, never()).save(any());
    }

    @Test
    @DisplayName("chargeBalance - 기존 잔액이 없을 때 새로 생성")
    void chargeBalance_notExists() {
        BalanceCommand.Charge command = BalanceCommand.Charge.of(2L, 1000L);
        when(balanceRepository.findOptionalByUserId(2L)).thenReturn(Optional.empty());

        balanceService.chargeBalance(command);
        ArgumentCaptor<Balance> captor = ArgumentCaptor.forClass(Balance.class);
        verify(balanceRepository).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(2L);
        assertThat(captor.getValue().getBalance()).isEqualTo(1000L);
    }

    @Test
    @DisplayName("useBalance - 기존 잔액이 있을 때 사용")
    void useBalance_exists() {
        Balance balance = spy(Balance.create(1L, 2000L));
        BalanceCommand.Use command = BalanceCommand.Use.of(1L, 500L);
        when(balanceRepository.findOptionalByUserId(1L)).thenReturn(Optional.of(balance));

        balanceService.useBalance(command);
        verify(balance).use(500L);
        verify(balanceRepository, never()).save(any());
    }

    @Test
    @DisplayName("useBalance - 기존 잔액이 없을 때 새로 생성")
    void useBalance_notExists() {
        BalanceCommand.Use command = BalanceCommand.Use.of(3L, 700L);
        when(balanceRepository.findOptionalByUserId(3L)).thenReturn(Optional.empty());

        balanceService.useBalance(command);
        ArgumentCaptor<Balance> captor = ArgumentCaptor.forClass(Balance.class);
        verify(balanceRepository).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(3L);
        assertThat(captor.getValue().getBalance()).isEqualTo(700L);
    }
} 