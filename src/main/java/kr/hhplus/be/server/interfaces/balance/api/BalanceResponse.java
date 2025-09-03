package kr.hhplus.be.server.interfaces.balance.api;

import kr.hhplus.be.server.domain.balance.BalanceInfo;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.Getter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BalanceResponse {

    @Getter
    @NoArgsConstructor
    public static class Balance {
        private Long amount;

        public Balance(Long amount) {
            this.amount = amount;
        }

        public static Balance of(BalanceInfo.Balance balance) {
            return new Balance(balance.getBalance());
        }
    }
} 