package kr.hhplus.be.server.infrastructure.rank;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.domain.rank.Rank;

public interface RankJpaRepository extends JpaRepository<Rank, Long> {
    
}
