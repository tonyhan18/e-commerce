package kr.hhplus.be.server.interfaces.balance;

import kr.hhplus.be.server.application.balance.BalanceResult;
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

        public static Balance of(BalanceResult.Balance balance) {
            return new Balance(balance.getBalance());
        }
    }
} 