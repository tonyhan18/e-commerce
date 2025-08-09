package kr.hhplus.be.server.application.balance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import kr.hhplus.be.server.domain.balance.Balance;
import kr.hhplus.be.server.domain.balance.BalanceRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.support.IntegrationTestSupport;

import static org.assertj.core.api.Assertions.*;

class BalanceFacadeIntegrationTest extends IntegrationTestSupport{
    @Autowired
    private BalanceFacade balanceFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BalanceRepository balanceRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.create("항플");
        userRepository.save(user);

        Balance balance = Balance.builder()
            .userId(user.getId())
            .balance(100_000L)
            .build();
        balanceRepository.save(balance);
    }

    @DisplayName("잔액을 충전한다.")
    @Test
    void chargeBalance() {
        // given
        BalanceCriteria.Charge criteria = BalanceCriteria.Charge.of(user.getId(), 10_000L);

        // when
        balanceFacade.chargeBalance(criteria);

        // then
        Balance balance = balanceRepository.findOptionalByUserId(user.getId()).orElseThrow();
        assertThat(balance.getBalance()).isEqualTo(110_000L);
    }

    @DisplayName("잔액을 조회한다.")
    @Test
    void getBalance() {
        // given
        Long userId = user.getId();

        // when
        BalanceResult.Balance balance = balanceFacade.getBalance(userId);

        // then
        assertThat(balance.getBalance()).isEqualTo(100_000L);
    }
} 