package kr.hhplus.be.server.interfaces.rank.api;

import java.time.LocalDate;

import kr.hhplus.be.server.domain.rank.RankCommand;
import kr.hhplus.be.server.domain.rank.RankInfo;
import kr.hhplus.be.server.domain.rank.RankService;
import kr.hhplus.be.server.interfaces.ApiResponse;
import kr.hhplus.be.server.interfaces.rank.RankResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RankController {

    private final RankService rankService;

    @GetMapping("/api/v1/products/ranks")
    public ApiResponse<RankResponse.PopularProducts> getPopularProducts() {
        RankInfo.PopularProducts popularProducts = rankService.cachedPopularProducts(RankCommand.PopularProducts.ofTop5Days3(LocalDate.now()));
        return ApiResponse.success(RankResponse.PopularProducts.of(popularProducts));
    }
}
