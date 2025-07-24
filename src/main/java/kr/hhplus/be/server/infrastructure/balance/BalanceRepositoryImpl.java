package kr.hhplus.be.server.infrastructure.balance;

import kr.hhplus.be.server.domain.balance.Balance;
import kr.hhplus.be.server.domain.balance.BalanceRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class BalanceRepositoryImpl implements BalanceRepository {
    @Override
    public Optional<Balance> findOptionalByUserId(Long userId) {
        // TODO: 실제 DB 연동 로직 구현
        return Optional.empty();
    }

    @Override
    public Balance save(Balance balance) {
        // TODO: 실제 DB 연동 로직 구현
        return balance;
    }
} 