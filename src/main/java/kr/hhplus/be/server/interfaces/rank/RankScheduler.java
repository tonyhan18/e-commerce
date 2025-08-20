package kr.hhplus.be.server.interfaces.rank;

import java.time.LocalDate;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import kr.hhplus.be.server.application.rank.RankCriteria;
import kr.hhplus.be.server.application.rank.RankFacade;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankScheduler {

    private final RankFacade rankFacade;

    @Scheduled(cron = "0 */5 * * * *")
    public void createDailyRank() {
        log.info("실시간 인기상품 캐싱 스케줄러 실행");
        try {
            rankFacade.updatePopularProducts(RankCriteria.PopularProducts.ofTop5Days3());
            log.info("실시간 인기상품 캐싱 스케줄러 완료");
        } catch (Exception e) {
            log.error("실시간 인기상품 캐싱 스케줄러 실행 중 오류 발생", e);
        }
    }
}
