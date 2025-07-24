package kr.hhplus.be.server.domain.balance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class BalanceTest {

    @Test
    @DisplayName("잔액 객체 생성 - 정상")
    void createBalance_success() {
        Balance balance = Balance.create(1L, 1000L);
        assertThat(balance.getUserId()).isEqualTo(1L);
        assertThat(balance.getBalance()).isEqualTo(1000L);
    }

    @Test
    @DisplayName("잔액 객체 생성 - 음수 잔액 예외")
    void createBalance_negativeBalance() {
        assertThatThrownBy(() -> Balance.create(1L, -100L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("잔액은 0원 이상이어야 합니다.");
    }

    @Test
    @DisplayName("잔액 충전 - 정상")
    void charge_success() {
        Balance balance = Balance.create(1L, 1000L);
        balance.charge(500L);
        assertThat(balance.getBalance()).isEqualTo(1500L);
    }

    @Test
    @DisplayName("잔액 충전 - 0 이하 금액 예외")
    void charge_zeroOrNegative() {
        Balance balance = Balance.create(1L, 1000L);
        assertThatThrownBy(() -> balance.charge(0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("충전 금액은 0원 이상이어야 합니다.");
        assertThatThrownBy(() -> balance.charge(-100L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("충전 금액은 0원 이상이어야 합니다.");
    }

    @Test
    @DisplayName("잔액 충전 - 최대 잔액 초과 예외")
    void charge_exceedMaxBalance() {
        Balance balance = Balance.create(1L, 9_999_900L);
        assertThatThrownBy(() -> balance.charge(200L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("최대 잔액 초과");
    }

    @Test
    @DisplayName("잔액 사용 - 정상")
    void use_success() {
        Balance balance = Balance.create(1L, 1000L);
        balance.use(500L);
        assertThat(balance.getBalance()).isEqualTo(500L);
    }

    @Test
    @DisplayName("잔액 사용 - 0 이하 금액 예외")
    void use_zeroOrNegative() {
        Balance balance = Balance.create(1L, 1000L);
        assertThatThrownBy(() -> balance.use(0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용 금액은 0원 이상이어야 합니다.");
        assertThatThrownBy(() -> balance.use(-100L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용 금액은 0원 이상이어야 합니다.");
    }

    @Test
    @DisplayName("잔액 사용 - 잔액 부족 예외")
    void use_insufficientBalance() {
        Balance balance = Balance.create(1L, 1000L);
        assertThatThrownBy(() -> balance.use(2000L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("잔액 부족");
    }
} 