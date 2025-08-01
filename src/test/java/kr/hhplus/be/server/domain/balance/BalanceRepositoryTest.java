package kr.hhplus.be.server.domain.balance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BalanceRepositoryTest {

    @Mock
    private BalanceRepository balanceRepository;

    private Balance testBalance;

    @BeforeEach
    void setUp() {
        testBalance = Balance.create(1L, 1000L);
    }

    @Test
    @DisplayName("사용자 ID로 잔액 조회 - 성공")
    void findOptionalByUserId_success() {
        // given
        Long userId = 1L;
        when(balanceRepository.findOptionalByUserId(userId))
                .thenReturn(Optional.of(testBalance));

        // when
        Optional<Balance> result = balanceRepository.findOptionalByUserId(userId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getUserId()).isEqualTo(userId);
        assertThat(result.get().getBalance()).isEqualTo(1000L);
        verify(balanceRepository, times(1)).findOptionalByUserId(userId);
    }

    @Test
    @DisplayName("사용자 ID로 잔액 조회 - 존재하지 않는 사용자")
    void findOptionalByUserId_notFound() {
        // given
        Long userId = 999L;
        when(balanceRepository.findOptionalByUserId(userId))
                .thenReturn(Optional.empty());

        // when
        Optional<Balance> result = balanceRepository.findOptionalByUserId(userId);

        // then
        assertThat(result).isEmpty();
        verify(balanceRepository, times(1)).findOptionalByUserId(userId);
    }

    @Test
    @DisplayName("잔액 저장 - 새로운 잔액 생성")
    void save_newBalance() {
        // given
        Balance newBalance = Balance.create(2L, 2000L);
        when(balanceRepository.save(any(Balance.class)))
                .thenReturn(newBalance);

        // when
        Balance savedBalance = balanceRepository.save(newBalance);

        // then
        assertThat(savedBalance).isNotNull();
        assertThat(savedBalance.getUserId()).isEqualTo(2L);
        assertThat(savedBalance.getBalance()).isEqualTo(2000L);
        verify(balanceRepository, times(1)).save(newBalance);
    }

    @Test
    @DisplayName("잔액 저장 - 기존 잔액 업데이트")
    void save_updateExistingBalance() {
        // given
        Balance existingBalance = Balance.create(1L, 1000L);
        existingBalance.charge(500L); // 잔액을 1500L로 변경
        
        when(balanceRepository.save(any(Balance.class)))
                .thenReturn(existingBalance);

        // when
        Balance savedBalance = balanceRepository.save(existingBalance);

        // then
        assertThat(savedBalance).isNotNull();
        assertThat(savedBalance.getUserId()).isEqualTo(1L);
        assertThat(savedBalance.getBalance()).isEqualTo(1500L);
        verify(balanceRepository, times(1)).save(existingBalance);
    }

    @Test
    @DisplayName("잔액 저장 - 충전 후 저장")
    void save_afterCharge() {
        // given
        Balance balance = Balance.create(1L, 1000L);
        balance.charge(300L);
        
        when(balanceRepository.save(any(Balance.class)))
                .thenReturn(balance);

        // when
        Balance savedBalance = balanceRepository.save(balance);

        // then
        assertThat(savedBalance.getBalance()).isEqualTo(1300L);
        verify(balanceRepository, times(1)).save(balance);
    }

    @Test
    @DisplayName("잔액 저장 - 사용 후 저장")
    void save_afterUse() {
        // given
        Balance balance = Balance.create(1L, 1000L);
        balance.use(200L);
        
        when(balanceRepository.save(any(Balance.class)))
                .thenReturn(balance);

        // when
        Balance savedBalance = balanceRepository.save(balance);

        // then
        assertThat(savedBalance.getBalance()).isEqualTo(800L);
        verify(balanceRepository, times(1)).save(balance);
    }

    @Test
    @DisplayName("잔액 저장 - 복합 연산 후 저장")
    void save_afterComplexOperations() {
        // given
        Balance balance = Balance.create(1L, 1000L);
        balance.charge(500L);  // 1500L
        balance.use(200L);     // 1300L
        balance.charge(100L);  // 1400L
        
        when(balanceRepository.save(any(Balance.class)))
                .thenReturn(balance);

        // when
        Balance savedBalance = balanceRepository.save(balance);

        // then
        assertThat(savedBalance.getBalance()).isEqualTo(1400L);
        verify(balanceRepository, times(1)).save(balance);
    }

    @Test
    @DisplayName("잔액 저장 - 최대 잔액 상태에서 저장")
    void save_maxBalance() {
        // given
        Balance balance = Balance.create(1L, 9_999_999L);
        when(balanceRepository.save(any(Balance.class)))
                .thenReturn(balance);

        // when
        Balance savedBalance = balanceRepository.save(balance);

        // then
        assertThat(savedBalance.getBalance()).isEqualTo(9_999_999L);
        verify(balanceRepository, times(1)).save(balance);
    }

    @Test
    @DisplayName("잔액 저장 - 0원 잔액 저장")
    void save_zeroBalance() {
        // given
        Balance balance = Balance.create(1L, 1000L);
        balance.use(1000L); // 잔액을 0원으로 만듦
        
        when(balanceRepository.save(any(Balance.class)))
                .thenReturn(balance);

        // when
        Balance savedBalance = balanceRepository.save(balance);

        // then
        assertThat(savedBalance.getBalance()).isEqualTo(0L);
        verify(balanceRepository, times(1)).save(balance);
    }
} 