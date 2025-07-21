package kr.hhplus.be.server.domain.balance;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BalanceTransactionType {
    CHARGE("충전"),
    USE("사용");

    private final String description;    
}
