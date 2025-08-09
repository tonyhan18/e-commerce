package kr.hhplus.be.server.domain.balance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import kr.hhplus.be.server.support.MockTestSupport;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class BalanceServiceTest extends MockTestSupport{
    @InjectMocks
    private BalanceService balanceService;

    @Mock
    private BalanceRepository balanceRepository;

    @DisplayName("잔고 충전 시, 충전 금액은 0보다 커야 한다.")
    @Test
    void chargeShouldPositiveAmount() {
        // given
        BalanceCommand.Charge command = BalanceCommand.Charge.of(1L, 0L);
        Balance balance = Balance.builder()
            .userId(1L)
            .balance(10_000L)
            .build();

        when(balanceRepository.findOptionalByUserId(anyLong()))
            .thenReturn(Optional.of(balance));

        // when & then
        assertThatThrownBy(() -> balanceService.chargeBalance(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("충전 금액은 0보다 커야 합니다.");
    }

    @DisplayName("잔고 충전 시, 최대 금액을 넘을 수 없다.")
    @Test
    void chargeCannotExceedMaxAmount() {
        // given
        BalanceCommand.Charge command = BalanceCommand.Charge.of(1L, 1L);
        Balance balance = Balance.builder()
            .userId(1L)
            .balance(10_000_000L)
            .build();

        when(balanceRepository.findOptionalByUserId(anyLong()))
            .thenReturn(Optional.of(balance));

        // when & then
        assertThatThrownBy(() -> balanceService.chargeBalance(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("최대 금액을 초과할 수 없습니다.");
    }

    @DisplayName("잔고가 없으면, 잔고를 생성한다.")
    @Test
    void chargeBalanceIfNotExist() {
        // given
        BalanceCommand.Charge command = BalanceCommand.Charge.of(1L, 10_000L);
        Balance balance = Balance.builder()
            .userId(1L)
            .balance(10_000L)
            .build();

        when(balanceRepository.findOptionalByUserId(anyLong()))
            .thenReturn(Optional.empty());

        when(balanceRepository.save(any(Balance.class)))
            .thenReturn(balance);

        // when
        balanceService.chargeBalance(command);

        // then
        verify(balanceRepository, times(1)).save(any(Balance.class));
    }

    @DisplayName("잔고가 있으면, 잔고를 충전한다.")
    @Test
    void chargeBalanceIfExist() {
        // given
        BalanceCommand.Charge command = mock(BalanceCommand.Charge.class);
        Balance balance = mock(Balance.class);

        when(balanceRepository.findOptionalByUserId(anyLong()))
            .thenReturn(Optional.of(balance));

        // when
        balanceService.chargeBalance(command);

        // then
        verify(balance, times(1)).charge(command.getAmount());
        verify(balanceRepository, times(0)).save(any(Balance.class));
    }

    @DisplayName("잔고가 없으면, 잔고를 차감하지 못한다.")
    @Test
    void useBalanceIfNotExist() {
        // given
        BalanceCommand.Use command = BalanceCommand.Use.of(1L, 10_000L);

        when(balanceRepository.findOptionalByUserId(anyLong()))
            .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> balanceService.useBalance(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("잔고가 존재하지 않습니다.");
    }

    @DisplayName("사용 금액이 0이면 잔고를 차감하지 못한다.")
    @Test
    void useBalanceWithZeroAmount() {
        // given
        BalanceCommand.Use command = BalanceCommand.Use.of(1L, 0L);
        Balance balance = Balance.builder()
            .userId(1L)
            .balance(10_000L)
            .build();

        when(balanceRepository.findOptionalByUserId(anyLong()))
            .thenReturn(Optional.of(balance));

        // when & then
        assertThatThrownBy(() -> balanceService.useBalance(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("사용 금액은 0보다 커야 합니다.");
    }

    @DisplayName("잔고가 부족하면, 잔고를 차감하지 못한다.")
    @Test
    void useBalanceWithInsufficientBalance() {
        // given
        BalanceCommand.Use command = BalanceCommand.Use.of(1L, 10_001L);
        Balance balance = Balance.builder()
            .userId(1L)
            .balance(10_000L)
            .build();

        when(balanceRepository.findOptionalByUserId(anyLong()))
            .thenReturn(Optional.of(balance));

        // when & then
        assertThatThrownBy(() -> balanceService.useBalance(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("잔액이 부족합니다.");
    }

    @DisplayName("잔고를 차감한다.")
    @Test
    void useBalance() {
        // given
        BalanceCommand.Use command = BalanceCommand.Use.of(1L, 10_000L);
        Balance balance = Balance.builder()
            .userId(1L)
            .balance(10_000L)
            .build();

        when(balanceRepository.findOptionalByUserId(anyLong()))
            .thenReturn(Optional.of(balance));

        // when
        balanceService.useBalance(command);

        // then
        assertThat(balance.getBalance()).isZero();
    }

    @DisplayName("잔고가 없으면 0을 반환한다.")
    @Test
    void getBalanceWithNotExist() {
        // given
        when(balanceRepository.findOptionalByUserId(anyLong()))
            .thenReturn(Optional.empty());

        // when
        BalanceInfo.Balance balanceInfo = balanceService.getBalance(1L);

        // then
        assertThat(balanceInfo.getBalance()).isZero();
    }

    @DisplayName("잔고를 조회한다.")
    @Test
    void getBalance() {
        // given
        Balance balance = Balance.builder()
            .userId(1L)
            .balance(10_000L)
            .build();

        when(balanceRepository.findOptionalByUserId(anyLong()))
            .thenReturn(Optional.of(balance));

        // when
        BalanceInfo.Balance balanceInfo = balanceService.getBalance(1L);

        // then
        assertThat(balanceInfo.getBalance()).isEqualTo(10_000L);
    }
} 