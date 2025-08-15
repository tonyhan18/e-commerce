package kr.hhplus.be.server.infrastructure.rank;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

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
