package kr.hhplus.be.server.domain.rank;

import java.util.List;

public interface RankRepository {

    Rank save(Rank rank);

    List<RankInfo.PopularProduct> findPopularSellRanks(RankCommand.PopularSellRank command);
}
