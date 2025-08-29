package kr.hhplus.be.server.domain.rank;

import java.time.LocalDate;
import java.util.List;

import kr.hhplus.be.server.domain.product.Product;

public interface RankRepository {

    Rank save(Rank rank);

    List<RankInfo.ProductScore> findPopularSellRanks(RankCommand.Query command);

    List<RankInfo.ProductScore> findDailyRank(RankKey key);

    List<Rank> findBy(RankType rankType, LocalDate date);

    void saveAll(List<Rank> ranks);

    boolean delete(RankKey key);

    Product findProductById(Long productId);
}
