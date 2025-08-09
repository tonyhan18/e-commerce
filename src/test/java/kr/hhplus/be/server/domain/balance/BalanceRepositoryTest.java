package kr.hhplus.be.server.domain.balance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.support.IntegrationTestSupport;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class BalanceRepositoryTest extends IntegrationTestSupport{

    @Autowired
    private BalanceRepository balanceRepository;

    @DisplayName("잔액을 저장한다.")
    @Test
    void save() {
        // given
        Balance balance = Balance.builder()
            .userId(1L)
            .balance(1_000L)
            .build();

        // when
        balanceRepository.save(balance);

        // then
        assertThat(balance.getId()).isNotNull();
    }

    @DisplayName("잔액이 없는 유저의 잔액을 조회한다.")
    @Test
    void findOptionalByUserId() {
        // when
        Optional<Balance> result = balanceRepository.findOptionalByUserId(1L);

        // then
        assertThat(result).isEmpty();
    }

    @DisplayName("잔액이 있는 유저의 잔액을 조회한다.")
    @Test
    void findByUserId() {
        // given
        Balance balance = Balance.builder()
            .userId(1L)
            .balance(1_000L)
            .build();
        balanceRepository.save(balance);

        // when
        Balance result = balanceRepository.findOptionalByUserId(balance.getUserId()).orElseThrow();

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getBalance()).isEqualTo(balance.getBalance());
    }
} 