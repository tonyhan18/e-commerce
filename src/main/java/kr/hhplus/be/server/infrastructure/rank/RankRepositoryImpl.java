package kr.hhplus.be.server.infrastructure.rank;

import kr.hhplus.be.server.domain.rank.Rank;
import kr.hhplus.be.server.domain.rank.RankCommand;
import kr.hhplus.be.server.domain.rank.RankInfo;
import kr.hhplus.be.server.domain.rank.RankRepository;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RankRepositoryImpl implements RankRepository {
    private final RankJpaRepository rankJpaRepository;
    private final RankQueryDslRepository rankQueryDslRepository;

    @Override
    public Rank save(Rank rank) {
        return rankJpaRepository.save(rank);
    }

    @Override
    public List<RankInfo.PopularProduct> findPopularSellRanks(RankCommand.PopularSellRank command) {
        return rankQueryDslRepository.findPopularSellRanks(command);
    }
}
