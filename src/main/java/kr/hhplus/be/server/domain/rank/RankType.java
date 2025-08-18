package kr.hhplus.be.server.domain.rank;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RankType {

    SELL("판매"),
    ;

    private final String description;
}
