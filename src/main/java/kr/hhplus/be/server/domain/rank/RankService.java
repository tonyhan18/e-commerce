package kr.hhplus.be.server.domain.rank;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.support.cache.CacheType;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankService {

    private final RankRepository rankRepository;

    @Transactional
    public List<Rank> createSellRank(RankCommand.CreateList command) {
        return command.getRanks().stream()
            .map(this::createSell)
            .map(rankRepository::save)
            .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheType.CacheName.POPULAR_PRODUCT, key = "'top:' + #command.top + ':days:' + #command.days")
    public RankInfo.PopularProducts cachedPopularProducts(RankCommand.PopularProducts command) {
        return getPopularProducts(command);
    }

    public RankInfo.PopularProducts getPopularProducts(RankCommand.PopularProducts command) {
        RankKey target = RankKey.ofDays(RankType.SELL, command.getDays());
        RankKeys sources = RankKeys.ofDaysWithDate(RankType.SELL, command.getDays(), command.getDate());

        RankCommand.Query query = RankCommand.Query.of(command.getTop(), target, sources);
        List<RankInfo.PopularProduct> popularProducts = rankRepository.findPopularSellRanks(query);

        return RankInfo.PopularProducts.of(popularProducts);
    }

    @Transactional
    public void persistDailyRank(LocalDate date) {
        RankKey key = RankKey.ofDate(RankType.SELL, date);
        List<RankInfo.ProductScore> productScores = rankRepository.findDailyRank(key);

        if (productScores.isEmpty()) {
            log.info("일일 판매 링크가 존재하지 않습니다. date: {}", date);
            return;
        }

        List<Rank> ranks = productScores.stream()
            .map(ps -> Rank.createSell(ps.getProductId(), date, ps.getTotalScore()))
            .toList();

        rankRepository.saveAll(ranks);
        rankRepository.delete(key);
    }

    private Rank createSell(RankCommand.Create command) {
        return Rank.createSell(command.getProductId(), command.getRankDate(), command.getScore());
    }

    private RankInfo.PopularProduct getProduct(RankInfo.ProductScore productScore) {
        Product product = rankRepository.findProductById(productScore.getProductId());
        return RankInfo.PopularProduct.of(product);
    }
}
