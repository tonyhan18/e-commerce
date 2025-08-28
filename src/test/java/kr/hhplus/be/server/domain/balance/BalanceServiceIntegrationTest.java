package kr.hhplus.be.server.domain.balance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.infrastructure.balance.repository.BalanceTransactionalJpaRepository;
import kr.hhplus.be.server.support.IntegrationTestSupport;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
class BalanceServiceIntegrationTest extends IntegrationTestSupport{

    @MockitoBean
    private BalanceClient balanceClient;

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private BalanceTransactionalJpaRepository balanceTransactionalJpaRepository;

    @DisplayName("잔고 충전 시, 사용자가 존재해야 한다.")
    @Test
    void chargeBalanceWhenUserDoesNotExist() {
        // given
        Long notExistUserId = 999L;
        BalanceCommand.Charge command = BalanceCommand.Charge.of(notExistUserId, 10_000L);

        when(balanceClient.getUser(notExistUserId))
            .thenThrow(new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // when & then
        assertThatThrownBy(() -> balanceService.chargeBalance(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @DisplayName("잔고 충전 시, 잔고가 존재할 때 충전 금액이 양수이어야 한다.")
    @Test
    void chargeBalanceWhenAmountIsNotPositive() {
        // given
        Long userId = 1L;
        Balance existingBalance = Balance.builder()
            .userId(userId)
            .balance(10_000L)
            .build();
        balanceRepository.save(existingBalance);

        BalanceCommand.Charge command = BalanceCommand.Charge.of(userId, -5_000L);

        // when & then
        assertThatThrownBy(() -> balanceService.chargeBalance(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("충전 금액은 0보다 커야 합니다.");
    }

    @DisplayName("잔고 충전 시, 최대 금액을 초과하면 예외를 발생시킨다.")
    @Test
    void chargeBalanceWhenExceedsMaxAmount() {
        // given
        Long userId = 1L;
        Balance existingBalance = Balance.builder()
            .userId(userId)
            .balance(10_000_000L)
            .build();
        balanceRepository.save(existingBalance);

        BalanceCommand.Charge command = BalanceCommand.Charge.of(userId, 1L);

        // when & then
        assertThatThrownBy(() -> balanceService.chargeBalance(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("최대 금액을 초과할 수 없습니다.");
    }

    @DisplayName("잔고 충전 시, 잔고가 없으면 새 잔고를 생성한다.")
    @Test
    void chargeBalanceWhenBalanceDoesNotExist() {
        // given
        Long userId = 1L;
        BalanceCommand.Charge command = BalanceCommand.Charge.of(userId, 5_000L);

        // when
        balanceService.chargeBalance(command);

        // then
        Balance newBalance = balanceRepository.findOptionalByUserId(userId).orElseThrow();
        assertThat(newBalance.getBalance()).isEqualTo(5_000L);
    }

    @DisplayName("잔고 생성 시, 최대 금액을 초과하면 예외를 발생시킨다.")
    @Test
    void createBalanceWhenExceedsMaxAmount() {
        // given
        Long userId = 1L;
        BalanceCommand.Charge command = BalanceCommand.Charge.of(userId, 10_000_001L);

        // when & then
        assertThatThrownBy(() -> balanceService.chargeBalance(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("최대 금액을 초과할 수 없습니다.");
    }

    @DisplayName("잔고 사용 시, 잔고가 존재하면 사용 금액을 차감한다.")
    @Test
    void useBalanceWhenBalanceExists() {
        // given
        Long userId = 1L;
        Balance existingBalance = Balance.builder()
            .userId(userId)
            .balance(10_000L)
            .build();
        balanceRepository.save(existingBalance);

        BalanceCommand.Use command = BalanceCommand.Use.of(userId, 5_000L);

        // when
        balanceService.useBalance(command);

        // then
        Balance updatedBalance = balanceRepository.findOptionalByUserId(userId).orElseThrow();
        assertThat(updatedBalance.getBalance()).isEqualTo(5_000L);
    }

    @DisplayName("잔고 사용 시, 잔고가 없으면 예외를 발생시킨다.")
    @Test
    void useBalanceWhenBalanceDoseNotExist() {
        // given
        Long userId = 1L;
        BalanceCommand.Use command = BalanceCommand.Use.of(userId, 5_000L);

        // when & then
        assertThatThrownBy(() -> balanceService.useBalance(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("잔고가 존재하지 않습니다.");
    }

    @DisplayName("잔고 사용 시, 사용 금액은 양수여야 한다.")
    @Test
    void useBalanceWhenAmountIsNotPositive() {
        // given
        Long userId = 1L;
        Balance existingBalance = Balance.builder()
            .userId(userId)
            .balance(10_000L)
            .build();
        balanceRepository.save(existingBalance);

        BalanceCommand.Use command = BalanceCommand.Use.of(userId, -5_000L);

        // when & then
        assertThatThrownBy(() -> balanceService.useBalance(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("사용 금액은 0보다 커야 합니다.");
    }

    @DisplayName("잔고 사용 시, 잔고 금액은 충분해야한다.")
    @Test
    void useBalanceWhenInsufficientBalance() {
        // given
        Long userId = 1L;
        Balance existingBalance = Balance.builder()
            .userId(userId)
            .balance(5_000L)
            .build();
        balanceRepository.save(existingBalance);

        BalanceCommand.Use command = BalanceCommand.Use.of(userId, 5_001L);

        // when & then
        assertThatThrownBy(() -> balanceService.useBalance(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("잔액이 부족합니다.");
    }

    @DisplayName("잔고 충전 & 사용 시, 트랜잭션 내역을 저장한다.")
    @Test
    void saveBalanceTransactionAfterChargeBalanceAndUseBalance() {
        // given
        Long userId = 1L;
        BalanceCommand.Charge command = BalanceCommand.Charge.of(userId, 5_000L);
        BalanceCommand.Use useCommand = BalanceCommand.Use.of(userId, 2_000L);

        // when
        balanceService.chargeBalance(command);
        balanceService.useBalance(useCommand);

        // then
        List<BalanceTransaction> transactions = balanceTransactionalJpaRepository.findAll();
        assertThat(transactions).hasSize(2)
            .extracting("amount", "transactionType")
            .containsExactly(
                tuple(5_000L, BalanceTransactionType.CHARGE),
                tuple(-2_000L, BalanceTransactionType.USE)
            );
    }

    @DisplayName("잔고 조회 시, 사용자가 존재해야 한다.")
    @Test
    void getBalanceWhenUserDoesNotExist() {
        // given
        Long notExistUserId = 999L;

        when(balanceClient.getUser(notExistUserId))
            .thenThrow(new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // when & then
        assertThatThrownBy(() -> balanceService.getBalance(notExistUserId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @DisplayName("잔고 조회 시, 잔고가 존재하면 잔고 정보를 반환한다.")
    @Test
    void getBalanceWhenBalanceExists() {
        // given
        Long userId = 1L;
        Balance existingBalance = Balance.builder()
            .userId(userId)
            .balance(10_000L)
            .build();
        balanceRepository.save(existingBalance);

        // when
        BalanceInfo.Balance balance = balanceService.getBalance(userId);

        // then
        assertThat(balance.getBalance()).isEqualTo(10_000L);
    }

    @DisplayName("잔고 조회 시, 잔고가 없으면 빈 잔고 정보를 반환한다.")
    @Test
    void getBalanceWhenBalanceDoseNotExist() {
        // given
        Long userId = 1L;

        // when
        BalanceInfo.Balance balance = balanceService.getBalance(userId);

        // then
        assertThat(balance.getBalance()).isZero();
    }
}