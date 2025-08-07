package kr.hhplus.be.server.infrastructure.balance;

import kr.hhplus.be.server.domain.balance.Balance;
import kr.hhplus.be.server.domain.balance.BalanceRepository;
import kr.hhplus.be.server.domain.balance.BalanceTransaction;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BalanceRepositoryImpl implements BalanceRepository {
    private final BalanceJpaRepository balanceJpaRepository;
    private final BalanceTransactionalJpaRepository balanceTransactionalJpaRepository;

    @Override
    public Optional<Balance> findOptionalByUserId(Long userId) {
        return balanceJpaRepository.findOptionalByUserId(userId);
    }

    @Override
    public Balance save(Balance balance) {
        return balanceJpaRepository.save(balance);
    }

    @Override
    public BalanceTransaction saveTransaction(BalanceTransaction balanceTransaction) {
        return balanceTransactionalJpaRepository.save(balanceTransaction);
    }
} 