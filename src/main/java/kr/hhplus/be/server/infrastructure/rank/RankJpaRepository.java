package kr.hhplus.be.server.infrastructure.rank;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.domain.rank.Rank;
import kr.hhplus.be.server.domain.rank.RankType;

public interface RankJpaRepository extends JpaRepository<Rank, Long> {
    List<Rank> findByRankTypeAndRankDate(RankType rankType, LocalDate rankDate);    
}
