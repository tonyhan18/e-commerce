package kr.hhplus.be.server.infrastructure.balance.repository;

import kr.hhplus.be.server.domain.balance.Balance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BalanceJpaRepository extends JpaRepository<Balance, Long> {
    Optional<Balance> findOptionalByUserId(Long userId);

    Balance save(Balance balance);
}
