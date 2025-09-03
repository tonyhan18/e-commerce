package kr.hhplus.be.server.infrastructure.balance.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.domain.balance.BalanceTransaction;

public interface BalanceTransactionalJpaRepository extends JpaRepository<BalanceTransaction, Long> {

}
